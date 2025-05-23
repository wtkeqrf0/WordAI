package com.sizov.wordai.screens.trainers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sizov.wordai.entities.LearnableDefinition
import com.sizov.wordai.repositoryImplementations.learnableDefinitionsRepository.CardsLearnableDefinitionsRepository
import com.sizov.wordai.trainer.ChangeStatisticsRepository
import com.sizov.wordai.trainer.TrainerCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainFlashcardsViewModel(
    dictionaryId: Long,
    cardsLearnDefRepository: CardsLearnableDefinitionsRepository,
    changeStatisticsRepository: ChangeStatisticsRepository,
) : ViewModel() {
    private val _viewState = MutableLiveData(TrainFlashcardsFragment.State.NOT_GUESSED)
    private val _currentWord: MutableLiveData<LearnableDefinition> = MutableLiveData()
    private var dictId: Long = 0
    private val trainerCards: TrainerCards = TrainerCards(cardsLearnDefRepository, changeStatisticsRepository)
    val viewState: LiveData<TrainFlashcardsFragment.State> = _viewState
    val currentWord: LiveData<LearnableDefinition> = _currentWord

    init {
        dictId = dictionaryId
        viewModelScope.launch(Dispatchers.IO) {
            // в onlyNotLearned ошибка, он работает наоборот
            trainerCards.loadDictionary(dictionaryId, onlyNotLearned = false)
            if (trainerCards.isDone) {
                _viewState.postValue(TrainFlashcardsFragment.State.DONE)
            } else {
                _currentWord.postValue(trainerCards.getNext())
            }
        }
    }

    fun restartDict() {
        _viewState.value = TrainFlashcardsFragment.State.NOT_GUESSED
        viewModelScope.launch(Dispatchers.IO) {
            trainerCards.loadDictionary(dictId, onlyNotLearned = true)
            val learnableDefinition = trainerCards.getNext()
            _currentWord.postValue(learnableDefinition)
        }
    }

    fun onCardPressed(currentState: TrainFlashcardsFragment.State) {
        _viewState.value = when (currentState) {
            TrainFlashcardsFragment.State.NOT_GUESSED -> TrainFlashcardsFragment.State.GUESSED_TRANSLATION
            TrainFlashcardsFragment.State.GUESSED_TRANSLATION -> TrainFlashcardsFragment.State.GUESSED_WORD
            TrainFlashcardsFragment.State.GUESSED_WORD -> TrainFlashcardsFragment.State.GUESSED_TRANSLATION
            TrainFlashcardsFragment.State.DONE -> TrainFlashcardsFragment.State.NOT_GUESSED
        }
    }

    fun onGuessPressed(guess: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            trainerCards.checkUserInput(guess)

            if (trainerCards.isDone) {
                _viewState.postValue(TrainFlashcardsFragment.State.DONE)
            } else {
                _viewState.postValue(TrainFlashcardsFragment.State.NOT_GUESSED)
                _currentWord.postValue(trainerCards.getNext())
            }
        }
    }
}
