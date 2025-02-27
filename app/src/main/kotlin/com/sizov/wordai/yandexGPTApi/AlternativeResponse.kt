package com.sizov.wordai.yandexGPTApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlternativeResponse(
    @SerialName("message")
    val message: MessageRequest?,

    @SerialName("status")
    val status: String?,
)
