package com.sizov.wordai.repositoryImplementations.dictionaryWordDefinitionsRepository

import com.sizov.wordai.entities.WordDefinition
import com.sizov.wordai.persistence.daos.DictionaryWordDefCrossRefDao
import com.sizov.wordai.persistence.daos.WordDefinitionsDao
import com.sizov.wordai.repositoryImplementations.extensions.toWordDefinition
import com.sizov.wordai.screens.dictionaries.dictionaryDetailsScreen.DictionaryWordDefinitionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DictWordDefinitionRepositoryImpl(
    private val wordDefinitionsDao: WordDefinitionsDao,
    private val crossRefDao: DictionaryWordDefCrossRefDao
) : DictionaryWordDefinitionsRepository {
    override suspend fun getDefinitionsByDictionaryId(
        dictionaryId: Long
    ): List<WordDefinition> = withContext(Dispatchers.IO) {
        wordDefinitionsDao.getDefinitionsByDictId(dictionaryId)
            .map { queryResult -> queryResult.toWordDefinition() }
    }

    override fun getDefinitionsFlowByDictId(dictionaryId: Long): Flow<List<WordDefinition>> {
        return wordDefinitionsDao.getDefinitionsFlowByDictId(dictionaryId)
            .map { definitionQueryResults ->
                definitionQueryResults
                    .map { queryResult -> queryResult.toWordDefinition() }
            }
    }

    override suspend fun getAllDefinitions(): List<WordDefinition> = withContext(Dispatchers.IO) {
        wordDefinitionsDao.getAllWordDefinitions()
            .map { queryResult -> queryResult.toWordDefinition() }
    }

    override suspend fun getWordDefinitionById(
        wordDefinitionId: Long
    ): WordDefinition? = withContext(Dispatchers.IO) {
        wordDefinitionsDao.getDefinitionById(wordDefinitionId)?.toWordDefinition()
    }

    override suspend fun deleteDefinitionsFromDictionary(
        dictionaryId: Long,
        definitions: List<WordDefinition>
    ) = withContext(Dispatchers.IO) {
        crossRefDao.deleteCrossRefsByIds(
            dictionaryId,
            definitions.map { it.id }
        )
        wordDefinitionsDao.deleteDefinitionsWithoutDict()
    }
}
