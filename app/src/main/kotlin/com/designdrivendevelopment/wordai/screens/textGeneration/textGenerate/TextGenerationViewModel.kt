package com.designdrivendevelopment.wordai.screens.textGeneration.textGenerate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.designdrivendevelopment.wordai.screens.dictionaries.DictionariesRepository
import kotlinx.coroutines.flow.MutableStateFlow

class TextGenerationViewModel(
    private val dictionariesRepository: DictionariesRepository
) : ViewModel() {
    private val _recognizedText: MutableLiveData<String> = MutableLiveData()

    val dictionariesFlow = dictionariesRepository.getAllDictionariesFlow()

    val recognizedText: LiveData<String>
        get() = _recognizedText

    fun onTextRecognized(text: String) {
        _recognizedText.value = text
    }
}
