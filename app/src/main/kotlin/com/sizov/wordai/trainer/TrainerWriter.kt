package com.sizov.wordai.trainer

import com.sizov.wordai.entities.LearnableDefinition
import com.sizov.wordai.screens.trainers.LearnableDefinitionsRepository
import com.sizov.wordai.trainer.utils.WordChangeArray
import com.sizov.wordai.trainer.utils.levenshteinDifference
import kotlin.math.roundToInt

const val WRITE_WEIGHT = 1f

class TrainerWriter(
    learnableDefinitionsRepository: LearnableDefinitionsRepository,
    changeStatisticsRepository: ChangeStatisticsRepository,
) :
    IteratorTrainerSingle<String>(
        learnableDefinitionsRepository,
        changeStatisticsRepository,
        WRITE_WEIGHT
    ) {

    /*
    expectedStr: cats
    userStr: cut
    curWordChange: [(c, KEEP), (a, REPLACE), (t, KEEP), (s, INSERT)]
     */
    var curWordChange: WordChangeArray = emptyArray()
        private set

    override fun rateEF(expectedWord: LearnableDefinition, userInput: String): Int {
        val (levenshteinDistance, path) = levenshteinDifference(
            expectedStr = expectedWord.writing,
            userStr = userInput
        )
        curWordChange = path
        // levenshteinDistance ∈ [0, max(expectedStr.length, userStr.length)]
        val errorRate = levenshteinDistance.toDouble() / expectedWord.writing.length
        val correctRate = (1 - minOf(1.0, errorRate)) // ∈ [0, 1]
        val normalizedCorrectRate = correctRate * (LearnableDefinition.GRADE_ARRAY.size - 1)

        return LearnableDefinition.GRADE_ARRAY[normalizedCorrectRate.roundToInt()]
    }
}
