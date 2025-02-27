package com.sizov.wordai.trainer

import com.sizov.wordai.entities.LearnableDefinition
import com.sizov.wordai.screens.trainers.LearnableDefinitionsRepository

const val CARDS_WEIGHT = 0.4f

class TrainerCards(
    learnableDefinitionsRepository: LearnableDefinitionsRepository,
    changeStatisticsRepository: ChangeStatisticsRepository,
) :
    IteratorTrainerSingle<Boolean>(
        learnableDefinitionsRepository,
        changeStatisticsRepository,
        CARDS_WEIGHT
    ) {

    override fun rateEF(expectedWord: LearnableDefinition, userInput: Boolean): Int {
        return if (userInput) {
            LearnableDefinition.GRADE_FOUR
        } else {
            0
        }
    }
}
