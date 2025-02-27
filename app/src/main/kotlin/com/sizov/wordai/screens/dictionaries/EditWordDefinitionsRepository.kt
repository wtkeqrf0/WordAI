package com.sizov.wordai.screens.dictionaries

import com.sizov.wordai.entities.Dictionary
import com.sizov.wordai.entities.WordDefinition

interface EditWordDefinitionsRepository {
    suspend fun addWordDefinition(wordDefinition: WordDefinition)

    suspend fun addNewWordDefinitionWithDictionaries(
        wordDefinition: WordDefinition,
        dictionaries: List<Dictionary>
    )

    suspend fun addDefinitionsToDictionary(
        definitions: List<WordDefinition>,
        dictionary: Dictionary
    )

    suspend fun deleteWordDefinition(wordDefinition: WordDefinition)

    suspend fun deleteWordDefinitions(wordDefinitions: List<WordDefinition>)

    suspend fun copyDefinitionToDictionary(
        wordDefinition: WordDefinition,
        dictionary: Dictionary
    ): Boolean
}
