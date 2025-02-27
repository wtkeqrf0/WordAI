package com.sizov.wordai.trainer

import com.sizov.wordai.entities.LearnableDefinition
import com.sizov.wordai.screens.trainers.LearnableDefinitionsRepository

abstract class IteratorTrainerSingle<CheckInputType>(
    learnableDefinitionsRepository: LearnableDefinitionsRepository,
    changeStatisticsRepository: ChangeStatisticsRepository,
    trainerWeight: Float,
) :
    CoreTrainer<LearnableDefinition, CheckInputType>(
        learnableDefinitionsRepository,
        changeStatisticsRepository,
        trainerWeight
    ) {
    public override fun getNext(): LearnableDefinition {
        return shuffledWords[this.currentIdx]
    }

    public override suspend fun checkUserInput(userInput: CheckInputType): Boolean {
        val currWord = shuffledWords[this.currentIdx]
        val scoreEF = rateEF(currWord, userInput)
        return handleAnswer(currWord, scoreEF)
    }

    abstract fun rateEF(expectedWord: LearnableDefinition, userInput: CheckInputType): Int
}
