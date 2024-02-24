package com.example.ai_language

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
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
    fun sendCallData(@Body contactsList: PhoneNumberDTO): Call<PhoneListDTO>

    @POST("send-news")
    fun sendNewsData(@Body data: NewsDTO): Call<NewsDTO>

    @GET("get-news") //뉴스 정보 가져옴
    fun getNews(): Call<List<NewsDTO>>

    @POST("login")
    fun login(@Body data: LoginRequestDTO): Call<LoginResponseDTO>

    @POST("confirm-nick")
    fun confirmNick(@Body data: ConfirmDTO): Observable<ConfirmedDTO>


    @POST("confirm-email")
    fun confirmEmail(@Body data: ConfirmDTO): Observable<ConfirmedDTO>


}