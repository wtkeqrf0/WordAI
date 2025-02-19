package com.designdrivendevelopment.wordai.persistence.queryResults

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.designdrivendevelopment.wordai.entities.Language
import com.designdrivendevelopment.wordai.persistence.roomEntities.ExampleEntity
import com.designdrivendevelopment.wordai.persistence.roomEntities.SynonymEntity
import com.designdrivendevelopment.wordai.persistence.roomEntities.TranslationEntity

data class WordDefinitionQueryResult(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "writing")
    val writing: String,
    @ColumnInfo(name = "part_of_speech")
    val partOfSpeech: String?,
    @ColumnInfo(name = "language")
    val language: Language,
    @ColumnInfo(name = "transcription")
    val transcription: String?,
    @ColumnInfo(name = "main_translation")
    val mainTranslation: String,
    @Relation(
        parentColumn = "id",
        entityColumn = "word_def_id"
    )
    val synonyms: List<SynonymEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "word_def_id"
    )
    val translations: List<TranslationEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "word_def_id"
    )
    val exampleEntities: List<ExampleEntity>,
)
