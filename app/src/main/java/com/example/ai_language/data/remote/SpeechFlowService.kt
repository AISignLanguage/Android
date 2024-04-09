package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.response.ResultTypeText
import com.example.ai_language.domain.model.response.TaskId
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface SpeechFlowService {
    @POST("asr/file/v1/create")
    @FormUrlEncoded
    suspend fun postTextByRemoteFile(
        @Header("keyId") keyId: String,
        @Header("keySecret") keySecret: String,
        @Field("lang") lang: String,
        @Field("remotePath") remotePath: String
    ): Response<TaskId>


    @Multipart
    @POST("asr/file/v1/create")
    suspend fun postTextByLocalFile(
        @Header("keyId") keyId: String,
        @Header("keySecret") keySecret: String,
        @Part("lang") lang: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<TaskId>


    @GET("asr/file/v1/query")
    suspend fun getTextFileBySpeechFlowApi(
        @Header("keyId") keyId: String,
        @Header("keySecret") keySecret: String,
        @Query("taskId") taskId: String,
        @Query("resultType") resultType : Int
    ): Response<ResultTypeText>
}