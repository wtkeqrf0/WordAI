package com.sizov.wordai.repositoryImplementations.extensions

import com.sizov.wordai.entities.Dictionary
import com.sizov.wordai.entities.ExampleOfDefinitionUse
import com.sizov.wordai.persistence.roomEntities.DictionaryEntity
import com.sizov.wordai.persistence.roomEntities.ExampleEntity

fun DictionaryEntity.toDictionary(size: Int): Dictionary {
    return Dictionary(
        id = this.id,
        label = this.label,
        isFavorite = this.isFavorite,
        size = size,
        language = this.language
    )
}

fun ExampleEntity.toExampleOfDefinitionUse(): ExampleOfDefinitionUse {
    return ExampleOfDefinitionUse(
        originalText = this.original,
        translatedText = this.translation
    )
}
