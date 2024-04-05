package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.request.ChangeNickNameDTO
import com.example.ai_language.domain.model.request.ChangeNickNameResultDTO
import com.example.ai_language.domain.model.request.GetProfileDTO
import com.example.ai_language.domain.model.request.ProfileRequestDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MyPageService {
    fun requestProfile(@Body data: ProfileRequestDTO): Call<GetProfileDTO>

    @POST("changeNickName")
    fun changeNickName(@Body data: ChangeNickNameDTO): Call<ChangeNickNameResultDTO>
}