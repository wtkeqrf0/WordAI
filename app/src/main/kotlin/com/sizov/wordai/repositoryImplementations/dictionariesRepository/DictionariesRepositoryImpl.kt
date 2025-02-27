package com.sizov.wordai.repositoryImplementations.dictionariesRepository

import android.util.Log
import com.sizov.wordai.entities.Dictionary
import com.sizov.wordai.entities.WordDefinition
import com.sizov.wordai.persistence.daos.DictionariesDao
import com.sizov.wordai.persistence.daos.DictionaryWordDefCrossRefDao
import com.sizov.wordai.persistence.daos.WordDefinitionsDao
import com.sizov.wordai.persistence.roomEntities.DictionaryWordDefCrossRef
import com.sizov.wordai.repositoryImplementations.extensions.toDictionary
import com.sizov.wordai.repositoryImplementations.extensions.toDictionaryEntity
import com.sizov.wordai.screens.dictionaries.DictionariesRepository
import com.sizov.wordai.screens.dictionaries.addDictionaryScreen.LanguageModel
import com.sizov.wordai.yandexDictApi.YandexDictionaryApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DictionariesRepositoryImpl(
    private val dictionariesDao: DictionariesDao,
    private val dictionaryWordDefCrossRefDao: DictionaryWordDefCrossRefDao,
    private val wordDefinitionsDao: WordDefinitionsDao,
    private val yandexDictApiService: YandexDictionaryApiService,
) : DictionariesRepository {
    override suspend fun getAllDictionaries(): List<Dictionary> = withContext(Dispatchers.IO) {
        dictionariesDao.getAll().map { dictionaryEntity ->
            val size = dictionariesDao.getDictionarySizeById(dictionaryEntity.id)
            dictionaryEntity.toDictionary(size)
        }
    }

    override fun getAllDictionariesFlow(): Flow<List<Dictionary>> {
        return dictionariesDao.getAllFlowInQueries().map { results ->
            results.map {
                Dictionary(
                    id = it.id,
                    label = it.label,
                    size = it.size,
                    isFavorite = it.isFavorite,
                    language = it.language
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getDictionaryById(
        dictionaryId: Long
    ): Dictionary = withContext(Dispatchers.IO) {
        val dictionaryEntity = dictionariesDao.getDictionaryById(dictionaryId)
        val size = dictionariesDao.getDictionarySizeById(dictionaryEntity.id)
        dictionaryEntity.toDictionary(size)
    }

    override suspend fun addDictionary(
        dictionary: Dictionary,
        addedWordDefinitions: List<WordDefinition>?
    ): Long = withContext(Dispatchers.IO) {
        val dictionaryId = dictionariesDao.insert(dictionary.toDictionaryEntity(entityId = 0))
        if (addedWordDefinitions != null) {
            val crossRefs = addedWordDefinitions.map { wordDefinition ->
                DictionaryWordDefCrossRef(
                    dictionaryId = dictionaryId,
                    wordDefinitionId = wordDefinition.id
                )
            }
            dictionaryWordDefCrossRefDao.insert(crossRefs)
        }
        return@withContext dictionaryId
    }

    override suspend fun updateDictionary(dictionary: Dictionary) = withContext(Dispatchers.IO) {
        dictionariesDao.update(dictionary.toDictionaryEntity(dictionary.id))
    }

    override suspend fun updateDictionaries(dictionaries: List<Dictionary>) = withContext(Dispatchers.IO) {
        dictionariesDao.update(dictionaries.map { it.toDictionaryEntity(it.id) })
    }

    override suspend fun deleteDictionary(dictionary: Dictionary) = withContext(Dispatchers.IO) {
        dictionariesDao.deleteById(dictionary.id)
        wordDefinitionsDao.deleteDefinitionsWithoutDict()
//        dictionaryWordDefCrossRefDao.deleteByDictionaryId(dictionary.id)
    }

    override suspend fun deleteDictionaries(dictionaries: List<Dictionary>) {
        dictionariesDao.deleteByIds(dictionaries.map { it.id })
        dictionaryWordDefCrossRefDao.deleteCrossRefByDictIds(dictionaries.map { it.id })
        wordDefinitionsDao.deleteDefinitionsWithoutDict()
    }

    override suspend fun deleteWordDefinitionFromDictionary(
        dictionary: Dictionary,
        wordDefinition: WordDefinition
    ) = withContext(Dispatchers.IO) {
        dictionaryWordDefCrossRefDao.deleteCrossRefByIds(
            dictionaryId = dictionary.id,
            wordDefinitionId = wordDefinition.id
        )
    }

    override suspend fun getLangs(): List<LanguageModel> {
        val url = "https://dictionary.yandex.net/api/v1/dicservice.json/getLangs"
        val rawListLangs = yandexDictApiService.getLangs(url)
        val langs = rawListLangs
            .filter { it.endsWith("-ru") && it != "ru" }
            .map { LanguageModel(title = it.substringBefore(delimiter = '-')) }
        Log.i("TOSH", langs.toString())
        return langs
    }
}
