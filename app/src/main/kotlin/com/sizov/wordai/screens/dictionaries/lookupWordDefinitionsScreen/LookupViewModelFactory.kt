package com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sizov.wordai.screens.dictionaries.DictionariesRepository
import com.sizov.wordai.screens.dictionaries.EditWordDefinitionsRepository
import com.sizov.wordai.screens.sharedWordDefProvider.SharedWordDefinitionProvider

class LookupViewModelFactory(
    private val lookupWordDefinitionsRepository: LookupWordDefinitionsRepository,
    private val editWordDefinitionsRepository: EditWordDefinitionsRepository,
    private val dictionariesRepository: DictionariesRepository,
    private val sharedWordDefinitionProvider: SharedWordDefinitionProvider,
    private val dictionaryId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LookupViewModel::class.java)) {
            return LookupViewModel(
                lookupWordDefinitionsRepository,
                editWordDefinitionsRepository,
                dictionariesRepository,
                sharedWordDefinitionProvider,
                dictionaryId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
