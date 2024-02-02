package com.example.ai_language

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Service {
    @GET("user")
    fun getUSer(): Call<UserDTO>

    @POST("save-user")
    fun saveUser(@Body jsonUser: UserDTO?): Call<ResponseBody?>?

    @POST("send-data") // 서버의 엔드포인트
    fun sendData(@Body data: UserDTO): Call<ResponseDTO> // 전송할 데이터와 응답 타입
}