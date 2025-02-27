package com.sizov.wordai.yandexGPTApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsageResponse(
    @SerialName("inputTextTokens")
    val inputTextTokens: Int?,

    @SerialName("completionTokens")
    val completionTokens: Int?,

    @SerialName("totalTokens")
    val totalTokens: Int?,

    @SerialName("completionTokensDetails")
    val completionTokensDetails: Int?
)
