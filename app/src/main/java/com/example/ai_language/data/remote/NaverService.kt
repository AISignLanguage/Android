package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.response.ResultPath
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverService {
    @GET("map-direction/v1/driving")
    suspend fun getRouteByOpenApi(
        @Header("X-NCP-APIGW-API-KEY-ID") apiKeyID: String = "wf7lge71wa",
        @Header("X-NCP-APIGW-API-KEY") apiKey: String = "S2xrrmnpETvuzVH4QolG12oY050t1GntJ8uxrl23",
        @Query("start") start: String,
        @Query("goal") goal: String,
    ): ResultPath
}