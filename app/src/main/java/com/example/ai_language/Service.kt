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
    fun sendData(@Body data: UserDTO): Call<LoginCheckDTO> // 전송할 데이터와 응답 타입

    @POST("send-callList") //CallListPage "접속할 때" 서버에 데이터 전달해줌 (페이지 들어갈 때마다 갱신되도록)
    fun sendCallData(@Body data: CallListDTO): Call<CallListDTO>

    @GET("get-callList") //모든 사용자들의 정보 조회 -> 가져옴 -> 뷰모델에 연결
    fun getCallData(): Call<CallListDTO>

    @GET("get-news") //뉴스 정보 가져옴
    fun getNews(): Call<List<NewsDTO>>

}