package com.sizov.wordai.entities

data class WordDefinitionStat(
    val id: Long,
    val writing: String,
    val skillLevel: Float,
    val numOfCompletedTrainings: Int,
    val numOfSuccessfullyTrainings: Int
)
