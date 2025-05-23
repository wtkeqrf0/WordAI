package com.sizov.wordai.persistence.prepopulating

import android.content.Context
import com.sizov.wordai.yandexDictApi.responses.DefinitionResponse
import com.sizov.wordai.yandexDictApi.responses.YandexDictionaryResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AssetsRepository(private val context: Context) {
    private val jsonFormat = Json { ignoreUnknownKeys = true }

    fun readDefinitionsFromAssets(): List<DefinitionResponse> {
        val definitionsJson = readAssetFileToString("data.json")
        val prepopulateData = jsonFormat
            .decodeFromString<List<YandexDictionaryResponse>>(definitionsJson)
        return prepopulateData.flatMap { it.definitions }
    }

    private fun readAssetFileToString(fileName: String): String {
        val stream = context.assets.open(fileName)
        return stream.bufferedReader().readText()
    }
}
