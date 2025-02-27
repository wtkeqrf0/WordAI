package com.sizov.wordai.screens.trainers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sizov.wordai.repositoryImplementations.learnableDefinitionsRepository.WriterLearnableDefinitionsRepository
import com.sizov.wordai.trainer.ChangeStatisticsRepository

class TrainWriteViewModelFactory(
    private val dictionaryId: Long,
    private val writerLearnableDefinitionsRepository: WriterLearnableDefinitionsRepository,
    private val changeStatisticsRepository: ChangeStatisticsRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(TrainWriteViewModel::class.java)) {
            return TrainWriteViewModel(
                dictionaryId,
                writerLearnableDefinitionsRepository,
                changeStatisticsRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
