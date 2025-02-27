package com.sizov.wordai.yandexDictApi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SynonymResponse(
    @SerialName("text")
    val writing: String
)
