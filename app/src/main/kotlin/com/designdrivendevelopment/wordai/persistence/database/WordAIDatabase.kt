package com.designdrivendevelopment.wordai.persistence.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.designdrivendevelopment.wordai.persistence.converters.DateConverter
import com.designdrivendevelopment.wordai.persistence.daos.CardsLearnableDefDao
import com.designdrivendevelopment.wordai.persistence.daos.DictionariesDao
import com.designdrivendevelopment.wordai.persistence.daos.DictionaryWordDefCrossRefDao
import com.designdrivendevelopment.wordai.persistence.daos.ExamplesDao
import com.designdrivendevelopment.wordai.persistence.daos.PairsLearnableDefDao
import com.designdrivendevelopment.wordai.persistence.daos.StatisticsDao
import com.designdrivendevelopment.wordai.persistence.daos.SynonymsDao
import com.designdrivendevelopment.wordai.persistence.daos.TranslationsDao
import com.designdrivendevelopment.wordai.persistence.daos.WordDefinitionsDao
import com.designdrivendevelopment.wordai.persistence.daos.WriterLearnableDefDao
import com.designdrivendevelopment.wordai.persistence.prepopulating.AssetsRepository
import com.designdrivendevelopment.wordai.persistence.roomEntities.DictionaryEntity
import com.designdrivendevelopment.wordai.persistence.roomEntities.DictionaryWordDefCrossRef
import com.designdrivendevelopment.wordai.persistence.roomEntities.ExampleEntity
import com.designdrivendevelopment.wordai.persistence.roomEntities.SynonymEntity
import com.designdrivendevelopment.wordai.persistence.roomEntities.TranslationEntity
import com.designdrivendevelopment.wordai.persistence.roomEntities.WordDefinitionEntity
import com.designdrivendevelopment.wordai.repositoryImplementations.extensions.getExampleEntities
import com.designdrivendevelopment.wordai.repositoryImplementations.extensions.getSynonymEntities
import com.designdrivendevelopment.wordai.repositoryImplementations.extensions.getTranslationEntities
import com.designdrivendevelopment.wordai.repositoryImplementations.extensions.getWordDefinitionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DictionaryEntity::class,
        ExampleEntity::class,
        SynonymEntity::class,
        TranslationEntity::class,
        WordDefinitionEntity::class,
        DictionaryWordDefCrossRef::class
    ],
    version = 2
)
@TypeConverters(DateConverter::class)
abstract class WordAIDatabase : RoomDatabase() {
    abstract val dictionariesDao: DictionariesDao
    abstract val examplesDao: ExamplesDao
    abstract val synonymsDao: SynonymsDao
    abstract val translationsDao: TranslationsDao
    abstract val wordDefinitionsDao: WordDefinitionsDao
    abstract val cardsLearnableDefDao: CardsLearnableDefDao
    abstract val writerLearnableDefDao: WriterLearnableDefDao
    abstract val pairsLearnableDefDao: PairsLearnableDefDao
    abstract val dictionaryWordDefCrossRefDao: DictionaryWordDefCrossRefDao
    abstract val statisticsDao: StatisticsDao

    companion object {
        private var database: WordAIDatabase? = null

        fun create(applicationContext: Context, coroutineScope: CoroutineScope): WordAIDatabase {
            val instance = Room.databaseBuilder(
                applicationContext,
                WordAIDatabase::class.java,
                "WordAI_db"
            )
                .fallbackToDestructiveMigration()
                .addCallback(PrepopulateCallback(applicationContext, coroutineScope))
                .addMigrations(Migration1To2())
                .build()

            database = instance
            Log.d("DATABASE", "database exists is ${database != null}")
            return instance
        }

        private class Migration1To2 : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE parts_of_speech")
            }
        }

        private class PrepopulateCallback(
            private val context: Context,
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            private fun prepopulate(context: Context, coroutineScope: CoroutineScope) {
                val wordDefinitionsDao = database?.wordDefinitionsDao
                val translationsDao = database?.translationsDao
                val synonymsDao = database?.synonymsDao
                val examplesDao = database?.examplesDao
                val dictionariesDao = database?.dictionariesDao
                val dictionaryWordDefCrossRefDao = database?.dictionaryWordDefCrossRefDao

                val assetsRepository = AssetsRepository(context)
                val definitions = assetsRepository.readDefinitionsFromAssets()
                coroutineScope.launch(Dispatchers.IO) {
                    val dictionaryId = dictionariesDao!!.insert(
                        DictionaryEntity(
                            id = 0,
                            label = "Базовый словарь",
                            isFavorite = false
                        )
                    )

                    definitions.forEach { definitionResponse ->
                        val writing = definitionResponse.writing
                        val transcription = definitionResponse.transcription

                        definitionResponse.translations.forEach { translationResponse ->
                            val wordDefinitionEntity = translationResponse
                                .getWordDefinitionEntity(writing, transcription)
                            val defId = wordDefinitionsDao!!.insert(wordDefinitionEntity)

                            val synonyms = translationResponse.getSynonymEntities(defId)
                            synonymsDao?.insert(synonyms)

                            val translations = translationResponse.getTranslationEntities(defId)
                            translationsDao?.insert(translations)

                            val examples = translationResponse.getExampleEntities(defId)
                            examplesDao?.insert(examples)

                            dictionaryWordDefCrossRefDao?.insert(
                                DictionaryWordDefCrossRef(
                                    dictionaryId = dictionaryId,
                                    wordDefinitionId = defId
                                )
                            )
                        }
                    }
                }
            }

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                prepopulate(context, scope)
            }
        }
    }
}
