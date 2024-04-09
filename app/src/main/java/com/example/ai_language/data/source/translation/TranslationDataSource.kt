package com.example.ai_language.data.source.translation

import android.util.Log
import com.example.ai_language.data.remote.SpeechFlowService
import com.example.ai_language.domain.model.response.ResultTypeText
import com.example.ai_language.domain.model.response.TaskId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class TranslationDataSource @Inject constructor(
    private val speechFlowService: SpeechFlowService
){
    fun postTextByRemoteFile(keyId :String, keySecret: String, lang: String, remotePath: String) : Flow<Response<TaskId>> = flow{
        val result = speechFlowService.postTextByRemoteFile(keyId,keySecret, lang, remotePath)
        emit(result)
    }.catch {
        Log.e("Post By RemoteFile Failure", it.message.toString())
    }

    fun postTextByLocalFile(keyId :String, keySecret: String, lang: RequestBody, file: MultipartBody.Part) : Flow<Response<TaskId>> = flow{
        val result = speechFlowService.postTextByLocalFile(keyId,keySecret, lang, file)
        emit(result)
    }.catch {
        Log.e("Post By LocalFile Failure", it.message.toString())
    }

    fun getTextFileBySpeechFlowApi(keyId : String, keySecret: String, taskId : String, resultType : Int) : Flow<Response<ResultTypeText>> = flow {
        val result = speechFlowService.getTextFileBySpeechFlowApi(keyId, keySecret, taskId, resultType)
        emit(result)
    }.catch{
        Log.e("Get Text File By Speech Flow Api Failure", it.message.toString())
    }
}