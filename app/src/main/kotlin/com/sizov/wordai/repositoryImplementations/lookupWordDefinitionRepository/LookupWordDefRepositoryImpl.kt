package com.sizov.wordai.repositoryImplementations.lookupWordDefinitionRepository

import com.sizov.wordai.BuildConfig
import com.sizov.wordai.entities.WordDefinition
import com.sizov.wordai.persistence.daos.WordDefinitionsDao
import com.sizov.wordai.repositoryImplementations.extensions.toWordDefinition
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.LookupWordDefinitionsRepository
import com.sizov.wordai.yandexDictApi.YandexDictionaryApiService
import com.sizov.wordai.yandexDictApi.responses.YandexDictionaryResponse
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

class LookupWordDefRepositoryImpl(
    private val yandexDictApiService: YandexDictionaryApiService,
    private val wordDefinitionsDao: WordDefinitionsDao,
) : LookupWordDefinitionsRepository {
    override fun loadDefinitionsByWriting(
        writing: String,
        fromLanguage: String,
    ): Flow<DefinitionsRequestResult> = flow {
        try {
            emit(DefinitionsRequestResult.Loading)
            val url = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup"
            val response: YandexDictionaryResponse =
                yandexDictApiService.lookupWord(
                    url = url,
                    key = BuildConfig.YANDEX_DICT_API_KEY,
                    wordWriting = writing,
                    directionOfTranslation = "$fromLanguage-ru"
                )
            val definitionsList: List<WordDefinition> = response
                .definitions.flatMap { definitionResponse ->
                    definitionResponse.translations.map { translationResponse ->
                        translationResponse.toWordDefinition(
                            definitionResponse.writing,
                            definitionResponse.transcription
                        )
                    }
                }
            emit(DefinitionsRequestResult.Success(definitionsList))
        } catch (e: IOException) {
            emit(DefinitionsRequestResult.Failure.Error(e.message.orEmpty()))
        } catch (e: HttpException) {
            emit(
                DefinitionsRequestResult.Failure.HttpError(
                    code = e.code(),
                    message = e.message()
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    override fun getSavedDefinitionsByWriting(
        writing: String,
        fromLanguage: String,
    ): Flow<List<WordDefinition>> {
        return wordDefinitionsDao.getFlowOfDefinitionsByWriting(writing).map { definitionsList ->
            definitionsList.map { wordDefinitionQueryResult ->
                wordDefinitionQueryResult.toWordDefinition()
            }
        }
    }
}
