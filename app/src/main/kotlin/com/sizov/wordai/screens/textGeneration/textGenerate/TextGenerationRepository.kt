package com.sizov.wordai.screens.textGeneration.textGenerate

import com.sizov.wordai.yandexGPTApi.YandexGenerateTextResponse

interface TextGenerationRepository {
    suspend fun generateText(subject: String, language: String): YandexGenerateTextResponse?
}
