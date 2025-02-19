package com.designdrivendevelopment.wordai.repositoryImplementations.getStatisticsRepository

import com.designdrivendevelopment.wordai.entities.AnswersStatistic
import com.designdrivendevelopment.wordai.entities.DictionaryStatistic
import com.designdrivendevelopment.wordai.persistence.daos.StatisticsDao
import com.designdrivendevelopment.wordai.repositoryImplementations.extensions.toAnswersStatistic
import com.designdrivendevelopment.wordai.repositoryImplementations.extensions.toDictionaryStatistic
import com.designdrivendevelopment.wordai.screens.profile.GetStatisticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetStatisticsRepositoryImpl(
    private val statisticsDao: StatisticsDao
) : GetStatisticsRepository {
    override fun getStatisticsForAllDict(): Flow<List<DictionaryStatistic>> {
        return statisticsDao.getDictionariesStatistic().map { queryResult ->
            queryResult.map { it.toDictionaryStatistic() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getAnswersStatistic(): Flow<AnswersStatistic> {
        return statisticsDao.getAnswersStatistic().map { it.toAnswersStatistic() }.flowOn(Dispatchers.IO)
    }
}
