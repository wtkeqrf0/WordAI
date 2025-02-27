package com.sizov.wordai.yandexGPTApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultResponse(
    @SerialName("alternatives")
    val alternatives: List<AlternativeResponse>?,

    @SerialName("usage")
    val usage: UsageResponse?,

    @SerialName("modelVersion")
    val modelVersion: String?
)
