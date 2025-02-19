package com.designdrivendevelopment.wordai.repositoryImplementations.lookupWordDefinitionRepository

import com.designdrivendevelopment.wordai.entities.WordDefinition
import com.designdrivendevelopment.wordai.persistence.daos.WordDefinitionsDao
import com.designdrivendevelopment.wordai.repositoryImplementations.extensions.toWordDefinition
import com.designdrivendevelopment.wordai.screens.dictionaries.lookupWordDefinitionsScreen.LookupWordDefinitionsRepository
import com.designdrivendevelopment.wordai.yandexDictApi.YandexDictionaryApiService
import com.designdrivendevelopment.wordai.yandexDictApi.responses.YandexDictionaryResponse
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
        writing: String
    ): Flow<DefinitionsRequestResult> = flow {
        try {
            emit(DefinitionsRequestResult.Loading)
            val response: YandexDictionaryResponse = yandexDictApiService.lookupWord(writing)
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
        writing: String
    ): Flow<List<WordDefinition>> {
        return wordDefinitionsDao.getFlowOfDefinitionsByWriting(writing).map { definitionsList ->
            definitionsList.map { wordDefinitionQueryResult ->
                wordDefinitionQueryResult.toWordDefinition()
            }
        }
    }
}
