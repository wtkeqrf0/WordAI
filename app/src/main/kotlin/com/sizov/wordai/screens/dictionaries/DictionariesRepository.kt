package com.sizov.wordai.screens.dictionaries

import com.sizov.wordai.entities.Dictionary
import com.sizov.wordai.entities.WordDefinition
import com.sizov.wordai.screens.dictionaries.addDictionaryScreen.LanguageModel
import kotlinx.coroutines.flow.Flow

interface DictionariesRepository {
    suspend fun getAllDictionaries(): List<Dictionary>

    fun getAllDictionariesFlow(): Flow<List<Dictionary>>

    suspend fun getDictionaryById(dictionaryId: Long): Dictionary

    suspend fun addDictionary(
        dictionary: Dictionary,
        addedWordDefinitions: List<WordDefinition>? = null
    ): Long

    suspend fun updateDictionary(dictionary: Dictionary)

    suspend fun updateDictionaries(dictionaries: List<Dictionary>)

    suspend fun deleteDictionary(dictionary: Dictionary)

    suspend fun deleteDictionaries(dictionaries: List<Dictionary>)

    suspend fun deleteWordDefinitionFromDictionary(
        dictionary: Dictionary,
        wordDefinition: WordDefinition
    )

    suspend fun getLangs(): List<LanguageModel>
}
