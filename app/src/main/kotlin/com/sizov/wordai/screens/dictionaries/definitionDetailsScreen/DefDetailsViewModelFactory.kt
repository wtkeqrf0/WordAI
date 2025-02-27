package com.sizov.wordai.screens.dictionaries.definitionDetailsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sizov.wordai.screens.dictionaries.DictionariesRepository
import com.sizov.wordai.screens.dictionaries.EditWordDefinitionsRepository
import com.sizov.wordai.screens.sharedWordDefProvider.SharedWordDefinitionProvider

class DefDetailsViewModelFactory(
    private val saveMode: Int,
    private val dictionaryId: Long,
    private val editWordDefRepository: EditWordDefinitionsRepository,
    private val dictionariesRepository: DictionariesRepository,
    private val sharedWordDefProvider: SharedWordDefinitionProvider
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DefDetailsViewModel::class.java)) {
            return DefDetailsViewModel(
                saveMode,
                dictionaryId,
                editWordDefRepository,
                dictionariesRepository,
                sharedWordDefProvider,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
