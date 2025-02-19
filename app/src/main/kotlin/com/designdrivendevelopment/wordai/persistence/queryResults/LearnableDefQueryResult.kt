package com.designdrivendevelopment.wordai.persistence.queryResults

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.designdrivendevelopment.wordai.persistence.roomEntities.ExampleEntity
import com.designdrivendevelopment.wordai.persistence.roomEntities.TranslationEntity
import java.util.Date

data class LearnableDefQueryResult(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "writing")
    val writing: String,
    @ColumnInfo(name = "part_of_speech")
    val partOfSpeech: String?,
    @ColumnInfo(name = "main_translation")
    val mainTranslation: String,
    @ColumnInfo(name = "next_repeat_date")
    val nextRepeatDate: Date,
    @ColumnInfo(name = "repetition_number")
    val repetitionNumber: Int,
    @ColumnInfo(name = "interval")
    val interval: Int,
    @ColumnInfo(name = "easiness_factor")
    val easinessFactor: Float,
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
