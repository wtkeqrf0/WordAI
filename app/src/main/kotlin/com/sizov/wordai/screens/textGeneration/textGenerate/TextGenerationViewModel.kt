package com.sizov.wordai.screens.textGeneration.textGenerate

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sizov.wordai.entities.Dictionary
import com.sizov.wordai.entities.WordDefinition
import com.sizov.wordai.repositoryImplementations.lookupWordDefinitionRepository.DefinitionsRequestResult
import com.sizov.wordai.screens.dictionaries.DictionariesRepository
import com.sizov.wordai.screens.dictionaries.EditWordDefinitionsRepository
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.LookupWordDefinitionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TextGenerationViewModel(
    private val textGenerationRepository: TextGenerationRepository,
    private val dictionariesRepository: DictionariesRepository,
    private val lookupWordDefRepository: LookupWordDefinitionsRepository,
    private val editWordDefinitionsRepository: EditWordDefinitionsRepository,
) : ViewModel() {
    private val _recognizedText: MutableLiveData<String> = MutableLiveData()

    val dictionariesFlow = dictionariesRepository.getAllDictionariesFlow()

    private val _lookupWordState: MutableStateFlow<DefinitionsRequestResult> =
        MutableStateFlow(DefinitionsRequestResult.Loading)
    val lookupWordState: StateFlow<DefinitionsRequestResult> = _lookupWordState

    private val _generateTextState: MutableStateFlow<TextState> = MutableStateFlow(TextState.Init)
    val generateTextState: StateFlow<TextState> = _generateTextState

    private var chosenDictionary: Dictionary? = null

    val recognizedText: LiveData<String>
        get() = _recognizedText

    fun onTextRecognized(text: String) {
        _recognizedText.value = text
    }

    fun clearStates() {
        _lookupWordState.value = DefinitionsRequestResult.Loading
        _generateTextState.value = TextState.Init
    }

    fun generateText(dictionary: Dictionary, subject: String) {
        chosenDictionary = dictionary

        viewModelScope.launch {
            val response = textGenerationRepository.generateText(subject = subject, chosenDictionary?.language ?: "en")

            Log.i("TOSH", "generateText() | response = $response")
            if (response == null) {
                _generateTextState.value = TextState.Error
                return@launch
            }

            Log.i("TOSH", "response = ${response.result.alternatives?.get(0)?.message?.text ?: "null"}")

            _generateTextState.value =
                TextState.Success(generatedText = response.result.alternatives?.get(0)?.message?.text ?: "null")
        }
    }

    fun getTranslations(word: String) {
        val flow = lookupWordDefRepository.loadDefinitionsByWriting(writing = word, fromLanguage = chosenDictionary?.language ?: "en")
        viewModelScope.launch {
            flow.collect {
                _lookupWordState.value = it
            }
        }
    }

    fun saveTranslationToDictionary(translation: WordDefinition) {
        chosenDictionary?.let { dictionary ->
            viewModelScope.launch(Dispatchers.IO) {
                editWordDefinitionsRepository
                    .addDefinitionsToDictionary(listOf(translation), dictionary)
            }
        }
    }

    sealed interface TextState {
        object Init : TextState
        data class Success(val generatedText: String) : TextState
        object Error : TextState
    }
}
