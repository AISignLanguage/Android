package com.example.ai_language.domain.repository

import com.example.ai_language.domain.model.response.ResultTypeText
import com.example.ai_language.domain.model.response.TaskId
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

interface TranslationRepository {
    suspend fun postTextByRemoteFile(keyId :String, keySecret: String, lang: String, remotePath: String) : Flow<Response<TaskId>>
    suspend fun postTextByLocalFile(keyId :String, keySecret: String, lang: RequestBody, file: MultipartBody.Part) : Flow<Response<TaskId>>
    suspend fun getTextFileBySpeechFlowApi(keyId : String, keySecret: String, taskId : String,resultType : Int) : Flow<Response<ResultTypeText>>
}