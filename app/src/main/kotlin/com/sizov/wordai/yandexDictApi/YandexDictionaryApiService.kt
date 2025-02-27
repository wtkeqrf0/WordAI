package com.sizov.wordai.yandexDictApi

import com.sizov.wordai.BuildConfig
import com.sizov.wordai.yandexDictApi.responses.YandexDictionaryResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface YandexDictionaryApiService {
    @GET
    suspend fun lookupWord(
        @Url url: String,
        @Query("key") key: String = BuildConfig.YANDEX_DICT_API_KEY,
        @Query("text") wordWriting: String,
        @Query("lang") directionOfTranslation: String = "en-ru",
    ): YandexDictionaryResponse
}
