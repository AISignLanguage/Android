package com.example.ai_language.domain.repository

import com.example.ai_language.domain.model.response.ResponseBodys
import com.example.ai_language.domain.model.response.WavUrlResponse
import com.example.ai_language.domain.model.response.Wavresponse
import kotlinx.coroutines.flow.Flow

interface FastApiRepository {
    suspend fun getWavUrl(youtube_url : WavUrlResponse): Flow<Wavresponse>

}