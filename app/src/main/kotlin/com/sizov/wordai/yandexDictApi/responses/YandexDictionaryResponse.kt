package com.sizov.wordai.yandexDictApi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YandexDictionaryResponse(
    @SerialName("def")
    val definitions: List<DefinitionResponse>
)
