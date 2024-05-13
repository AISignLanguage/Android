package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.request.ChangeNicknameRequestDTO
import com.example.ai_language.domain.model.request.ChangePasswordRequestDTO
import com.example.ai_language.domain.model.request.ChangePasswordResponseDTO
import com.example.ai_language.domain.model.request.CheckPasswordRequestDTO
import com.example.ai_language.domain.model.request.CheckPasswordResponseDTO
import com.example.ai_language.domain.model.request.ConfirmRequestDTO
import com.example.ai_language.domain.model.request.FindEmailRequestDTO
import com.example.ai_language.domain.model.request.FindPwdDTO
import com.example.ai_language.domain.model.request.FindPwdOk
import com.example.ai_language.domain.model.request.JoinDTO
import com.example.ai_language.domain.model.request.LoginCheckDTO
import com.example.ai_language.domain.model.request.NewsDTO
import com.example.ai_language.domain.model.request.PhoneListDTO
import com.example.ai_language.domain.model.request.PhoneNumberDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface Service {

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<ResponseBody>

    @GET("user")
    fun getUSer(): Call<JoinDTO>

    @POST("save-user")
    fun saveUser(@Body jsonUser: JoinDTO?): Call<ResponseBody?>?

    @POST("findEmail") // 아이디 찾기
    fun findEmail(@Body data: FindEmailRequestDTO): Call<ResponseBody>

    @POST("find-pwd") // 비밀번호 찾기
    fun findPwd(@Body data: FindPwdDTO): Call<FindPwdOk>

    @POST("send-data") // 서버의 엔드포인트
    fun sendData(@Body data: JoinDTO): Call<LoginCheckDTO> // 전송할 데이터와 응답 타입

    @POST("send-callList") // 전화번호부
    fun sendCallData(@Body contactsList: PhoneNumberDTO): Call<PhoneListDTO>

    @POST("send-news")
    fun sendNewsData(@Body data: NewsDTO): Call<NewsDTO>

    @GET("get-news") //뉴스 정보 가져옴
    fun getNews(): Call<List<NewsDTO>>

//    @POST("login")
//    fun login(@Body data: LoginRequestDTO): Call<LoginResponseDTO>

    @POST("confirm-nick") // 닉네임 중복 확인
    fun confirmNick(@Body data: ConfirmRequestDTO) : Call<ResponseBody>

    @POST("confirm-email") // 이메일(아이디) 중복 확인
    fun confirmEmail(@Body data: ConfirmRequestDTO) : Call<ResponseBody>

    @POST("checkPassword")
    fun checkPassword(@Body request: CheckPasswordRequestDTO): Call<CheckPasswordResponseDTO>

    @POST("changePassword")
    fun changePassword(@Body request: ChangePasswordRequestDTO): Call<ChangePasswordResponseDTO>

    @GET("userInfo") //유저 정보 불러오기
    fun requestProfile(): Call<ResponseBody>

    @POST("changeNickName")
    fun changeNickName(@Body data: ChangeNicknameRequestDTO): Call<ResponseBody>

    @DELETE ("deleteUser")
    fun deleteUser(): Call<ResponseBody>
}