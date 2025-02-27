package com.sizov.wordai.yandexGPTApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YandexGenerateTextResponse(
    @SerialName("result")
    val result: ResultResponse
)
