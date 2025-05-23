package com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sizov.wordai.entities.WordDefinition
import com.sizov.wordai.repositoryImplementations.lookupWordDefinitionRepository.DefinitionsRequestResult
import com.sizov.wordai.screens.dictionaries.DictionariesRepository
import com.sizov.wordai.screens.dictionaries.EditWordDefinitionsRepository
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.selection.stringKey
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.CategoryHeaderItem
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.ItemWithType
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.WordDefinitionItem
import com.sizov.wordai.screens.screensUtils.UiEvent
import com.sizov.wordai.screens.sharedWordDefProvider.SharedWordDefinitionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LookupViewModel(
    private val lookupWordDefRepository: LookupWordDefinitionsRepository,
    private val editWordDefinitionsRepository: EditWordDefinitionsRepository,
    private val dictionariesRepository: DictionariesRepository,
    private val sharedWordDefinitionProvider: SharedWordDefinitionProvider,
    private val dictionaryId: Long
) : ViewModel() {
    companion object {
        private const val HIDE_LOADING_DELAY = 500L
    }

    private val _foundDefinitions = MutableLiveData<List<ItemWithType>>(emptyList())
    private val _messageEvents = MutableLiveData<UiEvent.ShowMessage>()
    private val _selectionStates = MutableLiveData(false)
    private val _selectionSize = MutableLiveData(0)
    private val _dataLoadingEvents = MutableLiveData<UiEvent.Loading>(
        UiEvent.Loading.HideLoading(isHandled = true)
    )
    private var currentItems: List<ItemWithType> = emptyList()
    private val selectedDefinitions: MutableList<WordDefinition> = mutableListOf()
    private var isSelectionActive: Boolean = false
        set(value) {
            if (value != field) {
                _selectionStates.value = value
            }
            field = value
        }
    val foundDefinitions: LiveData<List<ItemWithType>> = _foundDefinitions
    val messageEvents: LiveData<UiEvent.ShowMessage> = _messageEvents
    val selectionStates: LiveData<Boolean> = _selectionStates
    val selectionSize: LiveData<Int> = _selectionSize
    val dataLoadingEvents: LiveData<UiEvent.Loading> = _dataLoadingEvents

    fun lookupByWriting(writing: String) {
        _dataLoadingEvents.value = UiEvent.Loading.ShowLoading()
        viewModelScope.launch(Dispatchers.IO) {
            val dictFromLanguage = dictionariesRepository.getDictionaryById(dictionaryId).language
            val flowFromRemote = lookupWordDefRepository.loadDefinitionsByWriting(writing, dictFromLanguage)
            val flowFromLocal = lookupWordDefRepository.getSavedDefinitionsByWriting(writing, dictFromLanguage)

            flowFromRemote.combine(flowFromLocal) { networkResult, localDefinitions ->
                Pair(networkResult, localDefinitions)
            }.collect { pair ->
                val networkResult = pair.first
                val localDefinitions = pair.second
                val remoteDefinitions: List<WordDefinition> = when (networkResult) {
                    is DefinitionsRequestResult.Failure.Error -> {
                        _messageEvents.postValue(
                            UiEvent.ShowMessage(
                                message = "Обнаружены проблемы с интернет соединением." +
                                    "Попробуйте повторить попытку"
                            )
                        )
                        delay(HIDE_LOADING_DELAY)
                        _dataLoadingEvents.postValue(UiEvent.Loading.HideLoading())
                        emptyList()
                    }

                    is DefinitionsRequestResult.Failure.HttpError -> {
                        _messageEvents.postValue(
                            UiEvent.ShowMessage(
                                message = "Упс, что-то пошло не так" +
                                    "Попробуйте повторить попытку"
                            )
                        )
                        delay(HIDE_LOADING_DELAY)
                        _dataLoadingEvents.postValue(UiEvent.Loading.HideLoading())
                        emptyList()
                    }

                    is DefinitionsRequestResult.Success -> {
                        val remoteDefinitions = networkResult.definitions
                        if (remoteDefinitions.isEmpty()) {
                            _messageEvents.postValue(
                                UiEvent.ShowMessage(
                                    message = "В словаре не найдены определения для данного слова"
                                )
                            )
                        }
                        delay(HIDE_LOADING_DELAY)
                        _dataLoadingEvents.postValue(UiEvent.Loading.HideLoading())
                        remoteDefinitions
                    }

                    is DefinitionsRequestResult.Loading -> {
                        emptyList()
                    }
                }

                currentItems = createItemsList(localDefinitions, remoteDefinitions)
                _foundDefinitions.postValue(currentItems)
            }
        }
    }

    fun setDisplayedDefinition(definition: WordDefinition?) {
        sharedWordDefinitionProvider.sharedWordDefinition = definition
    }

    fun notifyToEventIsHandled(event: UiEvent) {
        event.isHandled = true
    }

    fun onSelectionCleared() {
        isSelectionActive = false
        currentItems = currentItems.map { item ->
            if (item is WordDefinitionItem) {
                item.copy(isSelected = false, isPartOfSelection = false)
            } else {
                item
            }
        }
        _foundDefinitions.value = currentItems
    }

    fun onSelectionSizeChanged(size: Int) {
        _selectionSize.value = size
    }

    fun onItemSelectionChanged(itemKey: String, selected: Boolean) {
        fun handleWhenSelectionActive(
            currentItems: List<ItemWithType>,
            selectedItem: WordDefinitionItem,
            selected: Boolean
        ): List<ItemWithType> {
            if (selected) {
                selectedDefinitions.add(selectedItem.data)
            } else {
                selectedDefinitions.remove(selectedItem.data)
                if (selectedDefinitions.isEmpty()) {
                    isSelectionActive = false
                    return setPartOfSelectionForList(currentItems, false)
                }
            }
            return currentItems
        }

        val selectedItem = currentItems.find { item -> item.stringKey == itemKey }
        when (selectedItem) {
            is WordDefinitionItem -> {
                if (selectedItem.isSelected == selected) {
                    return
                }
                currentItems = currentItems.toMutableList().apply {
                    set(
                        index = currentItems.indexOf(selectedItem),
                        element = selectedItem.copy(isSelected = selected)
                    )
                }.toList()

                if (isSelectionActive) {
                    currentItems = handleWhenSelectionActive(currentItems, selectedItem, selected)
                } else {
                    if (selected) {
                        selectedDefinitions.add(selectedItem.data)
                        isSelectionActive = true
                        currentItems = setPartOfSelectionForList(currentItems, true)
                    } else {
                        return
                    }
                }

                _foundDefinitions.value = currentItems
            }
        }
    }

    fun saveSelectedDefinitions() {
        val definitions = selectedDefinitions.toList()
        viewModelScope.launch(Dispatchers.IO) {
            if (definitions.isNotEmpty()) {
                val dictionary = dictionariesRepository.getDictionaryById(dictionaryId)
                editWordDefinitionsRepository
                    .addDefinitionsToDictionary(definitions, dictionary)
                _messageEvents.postValue(
                    UiEvent.ShowMessage(message = "Определения добавлены в \"${dictionary.label}\"")
                )
            }
        }
    }

    private fun setPartOfSelectionForList(
        items: List<ItemWithType>,
        isPartOfSelection: Boolean
    ): List<ItemWithType> {
        return items.map { item ->
            if (item is WordDefinitionItem) {
                item.copy(isPartOfSelection = isPartOfSelection)
            } else {
                item
            }
        }
    }

    private fun createItemsList(
        localDefinitions: List<WordDefinition>,
        remoteDefinitions: List<WordDefinition>
    ): List<ItemWithType> {
        val items = mutableListOf<ItemWithType>()
        if (remoteDefinitions.isNotEmpty()) {
            items.add(CategoryHeaderItem(header = "Загруженные определения"))
            items.addAll(
                remoteDefinitions.map { definition ->
                    WordDefinitionItem(
                        definition,
                        isSelected = selectedDefinitions.contains(definition)
                    )
                }
            )
        }
        if (localDefinitions.isNotEmpty()) {
            items.add(CategoryHeaderItem(header = "Сохраненные определения"))
            items.addAll(
                localDefinitions.map { definition ->
                    WordDefinitionItem(
                        definition,
                        isSelected = selectedDefinitions.contains(definition)
                    )
                }
            )
        }
        return items.toList()
    }
}
