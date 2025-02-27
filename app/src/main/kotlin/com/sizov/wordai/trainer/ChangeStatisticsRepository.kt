package com.sizov.wordai.trainer

interface ChangeStatisticsRepository {
    suspend fun addSuccessfulResultToWordDef(wordDefinitionsId: Long)

    suspend fun addFailedResultToWordDef(wordDefinitionsId: Long)
}
