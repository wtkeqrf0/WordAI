package com.sizov.wordai.yandexGPTApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageRequest(
    @SerialName("role")
    val role: String?,

    @SerialName("text")
    val text: String?
)
