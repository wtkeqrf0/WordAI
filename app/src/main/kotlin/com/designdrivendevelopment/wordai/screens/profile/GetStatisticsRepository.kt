package com.designdrivendevelopment.wordai.screens.profile

import com.designdrivendevelopment.wordai.entities.AnswersStatistic
import com.designdrivendevelopment.wordai.entities.DictionaryStatistic
import kotlinx.coroutines.flow.Flow

interface GetStatisticsRepository {
    fun getStatisticsForAllDict(): Flow<List<DictionaryStatistic>>

    fun getAnswersStatistic(): Flow<AnswersStatistic>
}
