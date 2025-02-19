package com.designdrivendevelopment.wordai.repositoryImplementations.learnableDefinitionsRepository

import com.designdrivendevelopment.wordai.entities.LearnableDefinition
import com.designdrivendevelopment.wordai.persistence.daos.PairsLearnableDefDao
import com.designdrivendevelopment.wordai.repositoryImplementations.extensions.toLearnableDef
import com.designdrivendevelopment.wordai.screens.trainers.LearnableDefinitionsRepository
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PairsLearnableDefinitionsRepository(
    private val pairsLearnableDefDao: PairsLearnableDefDao
) : LearnableDefinitionsRepository {
    override suspend fun getAll(): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        pairsLearnableDefDao.getAllLearnableDefinitions().map { learnableDefQueryResult ->
            learnableDefQueryResult.toLearnableDef()
        }
    }

    override suspend fun getByDictionaryId(
        dictionaryId: Long
    ): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        pairsLearnableDefDao
            .getLearnableDefinitionsByDictId(dictionaryId)
            .map { learnableDefQueryResult ->
                learnableDefQueryResult.toLearnableDef()
            }
    }

    override suspend fun getByRepeatDate(
        repeatDate: Date
    ): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        pairsLearnableDefDao
            .getLearnableDefinitionsByDate(repeatDate.time)
            .map { learnableDefQueryResult ->
                learnableDefQueryResult.toLearnableDef()
            }
    }

    override suspend fun getByDictionaryIdAndRepeatDate(
        dictionaryId: Long,
        repeatDate: Date,
    ): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        pairsLearnableDefDao
            .getLearnableDefinitionsByDateAndDictId(repeatDate.time, dictionaryId)
            .map { learnableDefQueryResult ->
                learnableDefQueryResult.toLearnableDef()
            }
    }

    override suspend fun updateLearnableDefinition(
        wordDefinition: LearnableDefinition
    ) = withContext(Dispatchers.IO) {
        pairsLearnableDefDao.updateWordDefinition(
            wordDefinitionId = wordDefinition.definitionId,
            nextRepeatDateInMillis = wordDefinition.repeatDate.time,
            repetitionNumber = wordDefinition.repetitionNumber,
            interval = wordDefinition.interval,
            easinessFactor = wordDefinition.easinessFactor
        )
    }
}
