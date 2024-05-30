package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.request.GetProfileDTO
import com.example.ai_language.domain.model.request.ProfileRequestDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET

interface MyPageService {
    fun requestProfile(@Body data: ProfileRequestDTO): Call<GetProfileDTO>

    @GET("userInfo") //유저 정보 불러오기
    fun requestProfile(): Call<ResponseBody>
}