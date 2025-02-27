package com.sizov.wordai.yandexGPTApi

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface YandexGPTApiService {
    @POST("iam/v1/tokens")
    suspend fun getAccessToken(
        @Query("yandexPassportOauthToken") yandexPassportOauthToken: String,
    ): YandexAccessTokenResponse

    @Headers("Content-Type: application/json")
    @POST
    suspend fun generateText(
        @Url url: String,
        @Header("Authorization") accessToken: String,
        @Body request: YandexGenerateTextRequest
    ): YandexGenerateTextResponse
}
