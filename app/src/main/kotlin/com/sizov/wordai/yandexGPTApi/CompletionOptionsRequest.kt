package com.sizov.wordai.yandexGPTApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompletionOptionsRequest(
    @SerialName("temperature")
    val temperature: Float?,

    @SerialName("maxTokens")
    val maxTokens: Int?
)
