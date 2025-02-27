package com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen

import com.sizov.wordai.entities.WordDefinition
import com.sizov.wordai.repositoryImplementations.lookupWordDefinitionRepository.DefinitionsRequestResult
import kotlinx.coroutines.flow.Flow

interface LookupWordDefinitionsRepository {
    fun loadDefinitionsByWriting(writing: String, fromLanguage: String): Flow<DefinitionsRequestResult>

    fun getSavedDefinitionsByWriting(writing: String, fromLanguage: String): Flow<List<WordDefinition>>
}
