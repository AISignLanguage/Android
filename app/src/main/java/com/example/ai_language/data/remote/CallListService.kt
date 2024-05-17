package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.request.PhoneListDTO
import com.example.ai_language.domain.model.request.PhoneNumberDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface CallListService {
    @POST("send-callList") // 전화번호부
    suspend fun sendPhoneNumbers(
        @Body phoneNumberDTO: PhoneNumberDTO
    ): PhoneListDTO
}