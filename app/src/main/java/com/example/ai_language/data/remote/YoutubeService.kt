package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.response.YoutubeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeService {
    @GET("videos")
    suspend fun getYoutubeData(
        @Query("id") videoId: String,
        @Query("key") apiKey: String = "AIzaSyB-t3Z3XXjDMZi7MiG3Xgf-LkU49Ota7LI",
        @Query("part") part: String = "snippet,statistics"
    ): Response<YoutubeResponse>
}