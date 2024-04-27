package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.response.WavUrlResponse
import com.example.ai_language.domain.model.response.Wavresponse
import retrofit2.http.Body
import retrofit2.http.POST

interface FastApiService {
    @POST("extract-audio/")
    suspend fun getWavUrl(
        @Body youtube_url : WavUrlResponse
    ): Wavresponse
}