package com.sizov.wordai.yandexDictApi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YandexDictionaryLangsResponse(
    @SerialName("langs")
    val langs: List<String>
)
