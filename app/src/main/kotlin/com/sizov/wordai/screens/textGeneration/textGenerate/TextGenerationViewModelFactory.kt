package com.sizov.wordai.screens.textGeneration.textGenerate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sizov.wordai.screens.dictionaries.DictionariesRepository
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.LookupWordDefinitionsRepository

class TextGenerationViewModelFactory(
    private val textGenerationRepository: TextGenerationRepository,
    private val dictionariesRepository: DictionariesRepository,
    private val lookupWordDefinitionsRepository: LookupWordDefinitionsRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TextGenerationViewModel::class.java)) {
            return TextGenerationViewModel(textGenerationRepository, dictionariesRepository, lookupWordDefinitionsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
