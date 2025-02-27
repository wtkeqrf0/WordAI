package com.sizov.wordai.yandexGPTApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YandexAccessTokenResponse(
    @SerialName("iamToken")
    val accessToken: String?,

    @SerialName("expiresAt")
    val expiresAt: String?
)
