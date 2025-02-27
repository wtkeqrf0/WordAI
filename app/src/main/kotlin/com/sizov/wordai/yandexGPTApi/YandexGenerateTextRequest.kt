package com.sizov.wordai.yandexGPTApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YandexGenerateTextRequest(
    @SerialName("modelUri")
    val modelUri: String,

    @SerialName("completionOptions")
    val completionOptions: CompletionOptionsRequest,

    @SerialName("messages")
    val messages: List<MessageRequest>
)
