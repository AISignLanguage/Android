package com.example.ai_language

import io.reactivex.rxjava3.core.Observable
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

    @POST("find-id") // 아이디 찾기
    fun findId(@Body data: FindIdDTO): Call<GetIdDTO>

    @POST("find-pwd") // 비밀번호 찾기
    fun findPwd(@Body data: FindPwdDTO): Call<FindPwdOk>

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

    @POST("checkPassword")
    fun checkPassword(@Body request: CheckPasswordRequestDTO): Call<CheckPasswordResponseDTO>

    @POST("changePassword")
    fun changePassword(@Body request: ChangePasswordRequestDTO): Call<ChangePasswordResponseDTO>

    @POST("requestProfile") //유저 정보 불러오기
    fun requestProfile(@Body data: ProfileRequestDTO): Call<GetProfileDTO>

    @POST("changeNickName")
    fun changeNickName(@Body data: ChangeNickNameDTO): Call<ChangeNickNameResultDTO>

    @POST("deleteUser")
    fun deleteUser(@Body requestDTO: DeleteUserRequestDTO): Call<DeleteUserResponseDTO>
}