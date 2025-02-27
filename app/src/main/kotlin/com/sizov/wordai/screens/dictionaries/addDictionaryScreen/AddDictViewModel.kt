package com.sizov.wordai.screens.dictionaries.addDictionaryScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sizov.wordai.entities.Dictionary
import com.sizov.wordai.entities.WordDefinition
import com.sizov.wordai.screens.dictionaries.DictionariesRepository
import com.sizov.wordai.screens.dictionaries.dictionaryDetailsScreen.DictionaryWordDefinitionsRepository
import com.sizov.wordai.screens.screensUtils.capitalizeFirstChar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddDictViewModel(
    private val dictionariesRepository: DictionariesRepository,
    private val dictDefinitionsRepository: DictionaryWordDefinitionsRepository
) : ViewModel() {
    private val _allLanguages: MutableLiveData<List<LanguageModel>> = MutableLiveData()
    private val _allDefinitions: MutableLiveData<List<SelectableWordDefinition>> = MutableLiveData()
    private var unfilteredDefinitions: List<SelectableWordDefinition> = emptyList()
    private var selectedLanguage: LanguageModel? = null
    private var selectedDefinitions: MutableList<WordDefinition> = mutableListOf()
    val allLanguages: LiveData<List<LanguageModel>>
        get() = _allLanguages
    val allDefinitions: LiveData<List<SelectableWordDefinition>>
        get() = _allDefinitions

    init {
        loadAllLanguages()
        loadAllDefinitions()
    }

    private fun loadAllLanguages() {
        viewModelScope.launch(Dispatchers.IO) {
            val languages = dictionariesRepository.getLangs()
            _allLanguages.postValue(languages)
        }
    }

    private fun loadAllDefinitions() {
        viewModelScope.launch(Dispatchers.IO) {
            unfilteredDefinitions = dictDefinitionsRepository.getAllDefinitions()
                .map { SelectableWordDefinition(it) }
            _allDefinitions.postValue(unfilteredDefinitions)
        }
    }

    fun changeItemSelection(definition: WordDefinition, isSelected: Boolean) {
        if (isSelected) {
            selectedDefinitions.add(definition)
        } else {
            selectedDefinitions.remove(definition)
        }
        unfilteredDefinitions = unfilteredDefinitions.map { selectableDefinition ->
            if (selectableDefinition.def == definition) {
                selectableDefinition.copy(isSelected = isSelected)
            } else {
                selectableDefinition
            }
        }
        _allDefinitions.value = unfilteredDefinitions
    }

    fun changeLanguageItemSelection(languageModel: LanguageModel) {
        selectedLanguage = languageModel
        Log.i("TOSH", "selectedLanguage = $selectedLanguage")
    }

    fun filter(text: String) {
        if (text.isEmpty()) {
            _allDefinitions.value = unfilteredDefinitions
        } else {
            val filteredDictionaries = unfilteredDefinitions.filter { selectableDefinition ->
                selectableDefinition.def.writing.startsWith(text, ignoreCase = true)
            }
            _allDefinitions.value = filteredDictionaries
        }
    }

    suspend fun addDictionary(label: String): Long = withContext(Dispatchers.IO) {
        return@withContext dictionariesRepository.addDictionary(
            Dictionary(
                id = Dictionary.NEW_DICTIONARY_ID,
                label = label.capitalizeFirstChar(),
                size = Dictionary.SIZE_EMPTY,
                isFavorite = false,
                language = selectedLanguage?.title ?: "en"
            ),
            selectedDefinitions
        )
    }
}
