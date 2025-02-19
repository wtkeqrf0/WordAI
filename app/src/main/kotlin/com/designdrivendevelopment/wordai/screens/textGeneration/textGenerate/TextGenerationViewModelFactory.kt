package com.designdrivendevelopment.wordai.screens.textGeneration.textGenerate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.wordai.screens.dictionaries.DictionariesRepository

class TextGenerationViewModelFactory(
    private val dictionariesRepository: DictionariesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TextGenerationViewModel::class.java)) {
            return TextGenerationViewModel(dictionariesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
