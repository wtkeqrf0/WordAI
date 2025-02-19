package com.designdrivendevelopment.wordai.repositoryImplementations.lookupWordDefinitionRepository

import com.designdrivendevelopment.wordai.entities.WordDefinition

sealed class DefinitionsRequestResult {
    class Success(val definitions: List<WordDefinition>) : DefinitionsRequestResult()

    sealed class Failure(val errorMessage: String) : DefinitionsRequestResult() {
        class HttpError(val code: Int, message: String) : Failure(message)

        class Error(message: String) : Failure(message)
    }

    object Loading : DefinitionsRequestResult()
}
