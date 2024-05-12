package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.response.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleService {
    @GET("maps/api/directions/json")
    suspend fun getDirectionByGoogleApi(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("key") apiKey: String
    ): DirectionsResponse
}