package com.designdrivendevelopment.wordai.application

import android.content.Context
import com.designdrivendevelopment.wordai.R
import com.designdrivendevelopment.wordai.persistence.database.WordAIDatabase
import com.designdrivendevelopment.wordai.repositoryImplementations.changeStatisticsRepository.ChangeStatisticsRepositoryImpl
import com.designdrivendevelopment.wordai.repositoryImplementations.dictionariesRepository.DictionariesRepositoryImpl
import com.designdrivendevelopment.wordai.repositoryImplementations.dictionaryWordDefinitionsRepository.DictWordDefinitionRepositoryImpl
import com.designdrivendevelopment.wordai.repositoryImplementations.editWordDefnititionsRepository.EditWordDefRepositoryImpl
import com.designdrivendevelopment.wordai.repositoryImplementations.getStatisticsRepository.GetStatisticsRepositoryImpl
import com.designdrivendevelopment.wordai.repositoryImplementations.learnableDefinitionsRepository.CardsLearnableDefinitionsRepository
import com.designdrivendevelopment.wordai.repositoryImplementations.learnableDefinitionsRepository.PairsLearnableDefinitionsRepository
import com.designdrivendevelopment.wordai.repositoryImplementations.learnableDefinitionsRepository.WriterLearnableDefinitionsRepository
import com.designdrivendevelopment.wordai.repositoryImplementations.lookupWordDefinitionRepository.LookupWordDefRepositoryImpl
import com.designdrivendevelopment.wordai.screens.bottomNavigation.BottomNavigator
import com.designdrivendevelopment.wordai.screens.bottomNavigation.Tab
import com.designdrivendevelopment.wordai.screens.dictionaries.dictionariesScreen.DictionariesFragment
import com.designdrivendevelopment.wordai.screens.profile.ProfileFragment
import com.designdrivendevelopment.wordai.screens.textGeneration.textGenerate.TextGenerationFragment
import com.designdrivendevelopment.wordai.screens.sharedWordDefProvider.SharedWordDefProviderImpl
import com.designdrivendevelopment.wordai.yandexDictApi.RetrofitModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class AppComponent(applicationContext: Context) {
    val bottomNavigator by lazy {
        BottomNavigator(
            listOf(
                Tab("DICTIONARIES") { DictionariesFragment.newInstance() },
                Tab("TEXT_GENERATION") { TextGenerationFragment.newInstance() },
                Tab("PROFILE") { ProfileFragment.newInstance() },
            ),
            R.id.fragment_container
        )
    }

    private val yandexDictionaryApiService = RetrofitModule().yandexDictionaryService
    private val db = WordAIDatabase.create(applicationContext, CoroutineScope(Dispatchers.IO))
    val dictionariesRepository by lazy {
        DictionariesRepositoryImpl(
            db.dictionariesDao,
            db.dictionaryWordDefCrossRefDao,
            db.wordDefinitionsDao
        )
    }
    val dictDefinitionsRepository by lazy {
        DictWordDefinitionRepositoryImpl(db.wordDefinitionsDao, db.dictionaryWordDefCrossRefDao)
    }
    val cardsLearnDefRepository by lazy {
        CardsLearnableDefinitionsRepository(db.cardsLearnableDefDao)
    }
    val writerLearnDefRepository by lazy {
        WriterLearnableDefinitionsRepository(db.writerLearnableDefDao)
    }
    val pairsLearnDefRepository by lazy {
        PairsLearnableDefinitionsRepository(db.pairsLearnableDefDao)
    }
    val changeStatisticsRepositoryImpl by lazy {
        ChangeStatisticsRepositoryImpl(db.statisticsDao)
    }
    val statisticsRepository by lazy { GetStatisticsRepositoryImpl(db.statisticsDao) }
    val lookupWordDefRepository by lazy {
        LookupWordDefRepositoryImpl(
            yandexDictApiService = yandexDictionaryApiService,
            wordDefinitionsDao = db.wordDefinitionsDao
        )
    }
    val editWordDefinitionsRepository by lazy {
        EditWordDefRepositoryImpl(
            wordDefinitionsDao = db.wordDefinitionsDao,
            dictWordDefCrossRefDao = db.dictionaryWordDefCrossRefDao,
            translationsDao = db.translationsDao,
            synonymsDao = db.synonymsDao,
            examplesDao = db.examplesDao
        )
    }
    val sharedWordDefProvider by lazy { SharedWordDefProviderImpl() }
}
