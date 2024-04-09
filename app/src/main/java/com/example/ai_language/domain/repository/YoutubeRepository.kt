package com.example.ai_language.domain.repository

import com.example.ai_language.domain.model.response.ResultTypeText
import com.example.ai_language.domain.model.response.YoutubeResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface YoutubeRepository {
    suspend fun getYoutubeData(videoId: String, apiKey: String, part: String = "snippet,statistics") : Flow<Response<YoutubeResponse>>
}