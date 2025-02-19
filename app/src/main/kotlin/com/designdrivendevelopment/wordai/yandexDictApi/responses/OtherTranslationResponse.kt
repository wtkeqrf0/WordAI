package com.designdrivendevelopment.wordai.yandexDictApi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtherTranslationResponse(
    @SerialName("text")
    val writing: String
)
