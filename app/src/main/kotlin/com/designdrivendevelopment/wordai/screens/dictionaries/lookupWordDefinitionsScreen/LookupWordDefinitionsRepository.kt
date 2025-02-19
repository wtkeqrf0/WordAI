package com.designdrivendevelopment.wordai.screens.dictionaries.lookupWordDefinitionsScreen

import com.designdrivendevelopment.wordai.entities.WordDefinition
import com.designdrivendevelopment.wordai.repositoryImplementations.lookupWordDefinitionRepository.DefinitionsRequestResult
import kotlinx.coroutines.flow.Flow

interface LookupWordDefinitionsRepository {
    fun loadDefinitionsByWriting(writing: String): Flow<DefinitionsRequestResult>

    fun getSavedDefinitionsByWriting(writing: String): Flow<List<WordDefinition>>
}
