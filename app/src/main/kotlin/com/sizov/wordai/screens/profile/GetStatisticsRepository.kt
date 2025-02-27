package com.sizov.wordai.screens.profile

import com.sizov.wordai.entities.AnswersStatistic
import com.sizov.wordai.entities.DictionaryStatistic
import kotlinx.coroutines.flow.Flow

interface GetStatisticsRepository {
    fun getStatisticsForAllDict(): Flow<List<DictionaryStatistic>>

    fun getAnswersStatistic(): Flow<AnswersStatistic>
}
