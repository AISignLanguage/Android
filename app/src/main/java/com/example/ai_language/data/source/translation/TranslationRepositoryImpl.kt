package com.example.ai_language.data.source.translation

import com.example.ai_language.domain.model.response.ApiResponse
import com.example.ai_language.domain.model.response.ResultPath
import com.example.ai_language.domain.model.response.ResultTypeText
import com.example.ai_language.domain.model.response.TaskId
import com.example.ai_language.domain.repository.MapRepository
import com.example.ai_language.domain.repository.NaverRepository
import com.example.ai_language.domain.repository.TranslationRepository
import dagger.Module
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject


class TranslationRepositoryImpl @Inject constructor(
    private val translationDataSource: TranslationDataSource
) : TranslationRepository {
    override suspend fun postTextByRemoteFile(
        keyId: String,
        keySecret: String,
        lang: String,
        remotePath: String
    ): Flow<Response<TaskId>> = translationDataSource.postTextByRemoteFile(keyId, keySecret, lang, remotePath)

    override suspend fun postTextByLocalFile(
        keyId: String,
        keySecret: String,
        lang: RequestBody,
        file: MultipartBody.Part
    ): Flow<Response<TaskId>> = translationDataSource.postTextByLocalFile(keyId, keySecret, lang, file)

    override suspend fun getTextFileBySpeechFlowApi(
        keyId: String,
        keySecret: String,
        taskId: String,
        resultType : Int
    ): Flow<Response<ResultTypeText>> = translationDataSource.getTextFileBySpeechFlowApi(keyId, keySecret, taskId, resultType)
}