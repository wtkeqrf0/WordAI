package com.sizov.wordai.repositoryImplementations.generateText

import android.util.Log
import com.sizov.wordai.screens.textGeneration.textGenerate.TextGenerationRepository
import com.sizov.wordai.yandexGPTApi.CompletionOptionsRequest
import com.sizov.wordai.yandexGPTApi.Constants
import com.sizov.wordai.yandexGPTApi.MessageRequest
import com.sizov.wordai.yandexGPTApi.YandexGPTApiService
import com.sizov.wordai.yandexGPTApi.YandexGenerateTextRequest
import com.sizov.wordai.yandexGPTApi.YandexGenerateTextResponse
import retrofit2.HttpException
import java.io.IOException

class TextGenerationRepositoryImpl(
    val yandexGPTApiService: YandexGPTApiService,
) : TextGenerationRepository {

    override suspend fun generateText(subject: String, language: String): YandexGenerateTextResponse? {
        val accessToken = getAccessToken()

        try {
            return yandexGPTApiService.generateText(
                url = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion",
                accessToken = "Bearer $accessToken",
                request = YandexGenerateTextRequest(
                    modelUri = MODEL_URI,
                    completionOptions = CompletionOptionsRequest(
                        temperature = 0.3f,
                        maxTokens = 150
                    ),
                    messages = listOf(
                        MessageRequest(
                            role = ROLE_SYSTEM,
                            text = createTextQuery(subject, language)
                        )
                    )
                ),
//            return YandexGenerateTextResponse(
//                ResultResponse(
//                    alternatives = listOf(
//                        AlternativeResponse(
//                            message = MessageRequest(
//                                role = "system",
//                                text = "Let's go to the park!! Ok?"
//                            ),
//                            status = "OK"
//                        )
//                    ),
//                    modelVersion = "",
//                    usage = UsageResponse(1, 1, 1, 1)
//                )
            )
        } catch (e: IOException) {
            Log.e("TextGenerationRepositoryImpl", "Error in generateText()!")
            e.printStackTrace()
        } catch (e: HttpException) {
            Log.e("TextGenerationRepositoryImpl", "Error in generateText()!")
            e.printStackTrace()
        }

        return null
    }

    private suspend fun getAccessToken(): String {
        try {
            Log.i("TOSH", "Пытаемся получить токен")

            val token = yandexGPTApiService.getAccessToken(Constants.YANDEX_PASSPORT_OAUTH_TOKEN).accessToken ?: "null"

            Log.i("TOSH", "Токен получили! $token")
            return token
        } catch (e: IOException) {
            Log.e("TextGenerationRepositoryImpl", "Error in getAccessToken()!")
            e.printStackTrace()
        } catch (e: HttpException) {
            Log.e("TextGenerationRepositoryImpl", "Error in getAccessToken()!")
            e.printStackTrace()
        }

        return ""
    }

    private fun createTextQuery(subject: String, language: String): String {
        return "Сгенерируй текст на языке $language на тему $subject"
    }

    private companion object {
        const val MODEL_URI = "gpt://b1g83k1fgviutbv7stae/yandexgpt-lite"
        const val ROLE_SYSTEM = "system"
    }
}
