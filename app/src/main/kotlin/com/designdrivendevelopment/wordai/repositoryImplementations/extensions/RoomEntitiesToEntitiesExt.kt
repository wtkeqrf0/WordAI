package com.designdrivendevelopment.wordai.repositoryImplementations.extensions

import com.designdrivendevelopment.wordai.entities.Dictionary
import com.designdrivendevelopment.wordai.entities.ExampleOfDefinitionUse
import com.designdrivendevelopment.wordai.persistence.roomEntities.DictionaryEntity
import com.designdrivendevelopment.wordai.persistence.roomEntities.ExampleEntity

fun DictionaryEntity.toDictionary(size: Int): Dictionary {
    return Dictionary(
        id = this.id,
        label = this.label,
        isFavorite = this.isFavorite,
        size = size
    )
}

fun ExampleEntity.toExampleOfDefinitionUse(): ExampleOfDefinitionUse {
    return ExampleOfDefinitionUse(
        originalText = this.original,
        translatedText = this.translation
    )
}
