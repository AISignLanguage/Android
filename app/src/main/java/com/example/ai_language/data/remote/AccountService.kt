package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.request.ChangePasswordRequestDTO
import com.example.ai_language.domain.model.request.ChangePasswordResponseDTO
import com.example.ai_language.domain.model.request.CheckPasswordRequestDTO
import com.example.ai_language.domain.model.request.CheckPasswordResponseDTO
import com.example.ai_language.domain.model.request.ConfirmDTO
import com.example.ai_language.domain.model.request.ConfirmedDTO
import com.example.ai_language.domain.model.request.FindEmailRequestDTO
import com.example.ai_language.domain.model.request.FindPwdDTO
import com.example.ai_language.domain.model.request.FindPwdOk
import com.example.ai_language.domain.model.request.JoinDTO
import com.example.ai_language.domain.model.request.JoinOKDTO
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountService {

    // 회원가입
    @POST("join")
    suspend fun register(
        @Body data: JoinDTO
    ): JoinOKDTO

    @POST("findEmail") // 아이디 찾기
    fun findId(@Body data: FindEmailRequestDTO): Call<ResponseBody>?

    @POST("find-pwd") // 비밀번호 찾기
    fun findPwd(@Body data: FindPwdDTO): Call<FindPwdOk>

//    @POST("login")
//    suspend fun login(
//        @Body data: LoginRequestDTO
//    ): Call<LoginResponseDTO>

    @POST("login")
     fun login(
        @Body requestBody: RequestBody
    ): Response<ResponseBody>

    @POST("confirm-nick")
    fun confirmNick(@Body data: ConfirmDTO): Observable<ConfirmedDTO>

    @POST("confirm-email")
    fun confirmEmail(@Body data: ConfirmDTO): Observable<ConfirmedDTO>

    @POST("checkPassword")
    fun checkPassword(@Body request: CheckPasswordRequestDTO): Call<CheckPasswordResponseDTO>

    @POST("changePassword")
    fun changePassword(@Body request: ChangePasswordRequestDTO): Call<ChangePasswordResponseDTO>
}