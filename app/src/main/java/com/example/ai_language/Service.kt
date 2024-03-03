package com.example.ai_language

import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Service {
    @GET("user")
    fun getUSer(): Call<UserDTO>

    @POST("save-user")
    fun saveUser(@Body jsonUser: UserDTO?): Call<ResponseBody?>?


    @POST("send-data") // 서버의 엔드포인트
    fun sendData(@Body data: UserDTO): Call<LoginCheckDTO> // 전송할 데이터와 응답 타입

    @POST("send-callList") // 전화번호부
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


    @POST("password")
    fun password(@Body data: ChangePasswordRequestDTO): Call<ChangePasswordResponseDTO>

    @POST("checkPassword")
    fun checkPassword(@Body request: CheckPasswordRequestDTO): Call<CheckPasswordResponseDTO>
    @GET("getPassword")
    fun getPassword(@Query("email") email: String): Call<CheckPasswordResponseDTO>



}