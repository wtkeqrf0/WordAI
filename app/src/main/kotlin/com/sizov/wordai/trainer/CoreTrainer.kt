package com.sizov.wordai.trainer

import com.sizov.wordai.entities.LearnableDefinition
import com.sizov.wordai.screens.trainers.LearnableDefinitionsRepository
import java.util.Calendar

abstract class CoreTrainer<NextOutType, CheckInputType>(
    private val learnableDefinitionsRepository: LearnableDefinitionsRepository,
    private val changeStatisticsRepository: ChangeStatisticsRepository,
    private val trainerWeight: Float,
) {
    var currentIdx = 0
    var size = 0 // number of words which will the trainer give away
    val isDone: Boolean
        get() = currentIdx >= shuffledWords.size

    var shuffledWords = emptyList<LearnableDefinition>()
    private val repeatWordsSet = mutableSetOf<LearnableDefinition>()

    suspend fun loadDictionary(dictionaryId: Long, onlyNotLearned: Boolean) {
        shuffledWords = if (onlyNotLearned) {
            learnableDefinitionsRepository
                .getByDictionaryId(
                    dictionaryId = dictionaryId,
                )
        } else {
            learnableDefinitionsRepository
                .getByDictionaryIdAndRepeatDate(
                    dictionaryId = dictionaryId,
                    repeatDate = with(Calendar.getInstance()) {
                        time
                    }
                )
        }
        shuffledWords = shuffledWords.shuffled()
        repeatWordsSet.clear()

        currentIdx = 0
        size = shuffledWords.size
    }

    suspend fun handleAnswer(word: LearnableDefinition, scoreEF: Int): Boolean {
        word.changeEFBasedOnNewGrade(scoreEF, trainerWeight)
        val isRight = scoreEF >= LearnableDefinition.PASSING_GRADE
        learnableDefinitionsRepository.updateLearnableDefinition(word)
        if (isRight) {
            changeStatisticsRepository.addSuccessfulResultToWordDef(word.definitionId)
        } else {
            changeStatisticsRepository.addFailedResultToWordDef(word.definitionId)
            repeatWordsSet.add(word)
        }

        currentIdx += 1
        if ((currentIdx >= shuffledWords.size) && (repeatWordsSet.isNotEmpty())) {
            // begin to iterate over words which were guessed incorrectly
            shuffledWords = repeatWordsSet.toList().shuffled()
            repeatWordsSet.clear()
            currentIdx = 0
        }

        return isRight
    }

    /* returns the data for training */
    abstract fun getNext(): NextOutType

    /* checks user userInput and calls the methods
    'handleTrueAnswer()' and 'handleFalseAnswer()' inside itself */
    abstract suspend fun checkUserInput(userInput: CheckInputType): Boolean
}
