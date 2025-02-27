package com.sizov.wordai.repositoryImplementations.extensions

import com.sizov.wordai.entities.Dictionary
import com.sizov.wordai.entities.ExampleOfDefinitionUse
import com.sizov.wordai.entities.WordDefinition
import com.sizov.wordai.persistence.roomEntities.DictionaryEntity
import com.sizov.wordai.persistence.roomEntities.ExampleEntity
import com.sizov.wordai.persistence.roomEntities.SynonymEntity
import com.sizov.wordai.persistence.roomEntities.TranslationEntity
import com.sizov.wordai.persistence.roomEntities.WordDefinitionEntity
import java.util.Calendar

fun Dictionary.toDictionaryEntity(entityId: Long): DictionaryEntity {
    return DictionaryEntity(
        id = entityId,
        label = this.label,
        isFavorite = this.isFavorite,
        language = this.language
    )
}

fun WordDefinition.toWordDefinitionEntity(): WordDefinitionEntity {
    return WordDefinitionEntity(
        id = 0,
        writing = writing,
        partOfSpeech = this.partOfSpeech,
        transcription = transcription,
        language = language,
        mainTranslation = mainTranslation,
        cardsNextRepeatDate = with(Calendar.getInstance()) { time },
        cardsRepetitionNumber = 0,
        cardsInterval = 1,
        writerRepeatDate = with(Calendar.getInstance()) { time },
        writerRepetitionNumber = 0,
        writerInterval = 1,
        pairsNextRepeatDate = with(Calendar.getInstance()) { time },
        pairsRepetitionNumber = 0,
        pairsInterval = 1,
        easinessFactor = 2.5F
    )
}

fun String.toSynonymEntity(wordDefId: Long): SynonymEntity {
    return SynonymEntity(
        wordDefinitionId = wordDefId,
        writing = this
    )
}

fun String.toTranslationEntity(wordDefId: Long): TranslationEntity {
    return TranslationEntity(
        wordDefinitionId = wordDefId,
        translation = this
    )
}

fun ExampleOfDefinitionUse.toExampleEntity(wordDefId: Long): ExampleEntity {
    return ExampleEntity(
        wordDefinitionId = wordDefId,
        original = originalText,
        translation = translatedText
    )
}
