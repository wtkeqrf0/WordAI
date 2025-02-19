package com.designdrivendevelopment.wordai.trainer

class MockCSR : ChangeStatisticsRepository {
    override suspend fun addSuccessfulResultToWordDef(wordDefinitionsId: Long) {
        // we do not use this method for tests
    }

    override suspend fun addFailedResultToWordDef(wordDefinitionsId: Long) {
        // we do not use this method for tests
    }
}
