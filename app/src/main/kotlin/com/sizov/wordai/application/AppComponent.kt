package com.sizov.wordai.application

import android.content.Context
import com.sizov.wordai.R
import com.sizov.wordai.persistence.database.WordAIDatabase
import com.sizov.wordai.repositoryImplementations.changeStatisticsRepository.ChangeStatisticsRepositoryImpl
import com.sizov.wordai.repositoryImplementations.dictionariesRepository.DictionariesRepositoryImpl
import com.sizov.wordai.repositoryImplementations.dictionaryWordDefinitionsRepository.DictWordDefinitionRepositoryImpl
import com.sizov.wordai.repositoryImplementations.editWordDefnititionsRepository.EditWordDefRepositoryImpl
import com.sizov.wordai.repositoryImplementations.generateText.TextGenerationRepositoryImpl
import com.sizov.wordai.repositoryImplementations.getStatisticsRepository.GetStatisticsRepositoryImpl
import com.sizov.wordai.repositoryImplementations.learnableDefinitionsRepository.CardsLearnableDefinitionsRepository
import com.sizov.wordai.repositoryImplementations.learnableDefinitionsRepository.PairsLearnableDefinitionsRepository
import com.sizov.wordai.repositoryImplementations.learnableDefinitionsRepository.WriterLearnableDefinitionsRepository
import com.sizov.wordai.repositoryImplementations.lookupWordDefinitionRepository.LookupWordDefRepositoryImpl
import com.sizov.wordai.screens.bottomNavigation.BottomNavigator
import com.sizov.wordai.screens.bottomNavigation.Tab
import com.sizov.wordai.screens.dictionaries.dictionariesScreen.DictionariesFragment
import com.sizov.wordai.screens.profile.ProfileFragment
import com.sizov.wordai.screens.textGeneration.textGenerate.TextGenerationFragment
import com.sizov.wordai.screens.sharedWordDefProvider.SharedWordDefProviderImpl
import com.sizov.wordai.yandexDictApi.RetrofitModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class AppComponent(applicationContext: Context) {
    val bottomNavigator by lazy {
        BottomNavigator(
            listOf(
                Tab("DICTIONARIES") { DictionariesFragment.newInstance() },
                Tab("RECOGNIZE") { TextGenerationFragment.newInstance() },
                Tab("PROFILE") { ProfileFragment.newInstance() },
            ),
            R.id.fragment_container
        )
    }

    private val yandexDictionaryApiService = RetrofitModule().yandexDictionaryService
    private val yandexGPTApiService = RetrofitModule().yandexGPTService

    private val db = WordAIDatabase.create(applicationContext, CoroutineScope(Dispatchers.IO))
    val dictionariesRepository by lazy {
        DictionariesRepositoryImpl(
            db.dictionariesDao,
            db.dictionaryWordDefCrossRefDao,
            db.wordDefinitionsDao,
            yandexDictionaryApiService
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
    val textGenerationRepository by lazy {
        TextGenerationRepositoryImpl(
            yandexGPTApiService = yandexGPTApiService
        )
    }
}
