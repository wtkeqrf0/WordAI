package com.designdrivendevelopment.wordai.repositoryImplementations.learnableDefinitionsRepository

import com.designdrivendevelopment.wordai.entities.LearnableDefinition
import com.designdrivendevelopment.wordai.persistence.daos.CardsLearnableDefDao
import com.designdrivendevelopment.wordai.repositoryImplementations.extensions.toLearnableDef
import com.designdrivendevelopment.wordai.screens.trainers.LearnableDefinitionsRepository
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CardsLearnableDefinitionsRepository(
    private val cardsLearnableDefDao: CardsLearnableDefDao
) : LearnableDefinitionsRepository {
    override suspend fun getAll(): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        cardsLearnableDefDao.getAllLearnableDefinitions().map { learnableDefQueryResult ->
            learnableDefQueryResult.toLearnableDef()
        }
    }

    override suspend fun getByDictionaryId(
        dictionaryId: Long
    ): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        cardsLearnableDefDao
            .getLearnableDefinitionsByDictId(dictionaryId)
            .map { learnableDefQueryResult ->
                learnableDefQueryResult.toLearnableDef()
            }
    }

    override suspend fun getByRepeatDate(
        repeatDate: Date
    ): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        cardsLearnableDefDao
            .getLearnableDefinitionsByDate(repeatDate.time)
            .map { learnableDefQueryResult ->
                learnableDefQueryResult.toLearnableDef()
            }
    }

    override suspend fun getByDictionaryIdAndRepeatDate(
        dictionaryId: Long,
        repeatDate: Date,
    ): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        cardsLearnableDefDao
            .getLearnableDefinitionsByDateAndDictId(repeatDate.time, dictionaryId)
            .map { learnableDefQueryResult ->
                learnableDefQueryResult.toLearnableDef()
            }
    }

    override suspend fun updateLearnableDefinition(
        wordDefinition: LearnableDefinition
    ) = withContext(Dispatchers.IO) {
        cardsLearnableDefDao.updateWordDefinition(
            wordDefinitionId = wordDefinition.definitionId,
            nextRepeatDateInMillis = wordDefinition.repeatDate.time,
            repetitionNumber = wordDefinition.repetitionNumber,
            interval = wordDefinition.interval,
            easinessFactor = wordDefinition.easinessFactor
        )
    }
}
