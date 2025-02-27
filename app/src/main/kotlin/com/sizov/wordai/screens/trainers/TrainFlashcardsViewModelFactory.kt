package com.sizov.wordai.screens.trainers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sizov.wordai.repositoryImplementations.learnableDefinitionsRepository.CardsLearnableDefinitionsRepository
import com.sizov.wordai.trainer.ChangeStatisticsRepository

class TrainFlashcardsViewModelFactory(
    private val dictionaryId: Long,
    private val cardsLearnDefRepository: CardsLearnableDefinitionsRepository,
    private val changeStatisticsRepository: ChangeStatisticsRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(TrainFlashcardsViewModel::class.java)) {
            return TrainFlashcardsViewModel(dictionaryId, cardsLearnDefRepository, changeStatisticsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
