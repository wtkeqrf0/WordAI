package com.sizov.wordai.yandexDictApi

import android.util.Log
import com.sizov.wordai.BuildConfig
import com.sizov.wordai.yandexGPTApi.YandexGPTApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create

class RetrofitModule {
    private val logsInterceptor = HttpLoggingInterceptor().also {
        it.setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val okHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(logsInterceptor)
        .build()
    private val contentType = "application/json".toMediaType()
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://iam.api.cloud.yandex.net") // https://dictionary.yandex.net/api/v1/dicservice.json/
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    val yandexDictionaryService = retrofit.create<YandexDictionaryApiService>()
    val yandexGPTService = retrofit.create<YandexGPTApiService>()

    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            Log.i("TOSH", "request = $originalRequest")
            val originalRequestUrl = originalRequest.url

            return chain.proceed(originalRequest)
        }
    }
}
