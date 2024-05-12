package com.example.ai_language.domain.repository

import com.example.ai_language.domain.model.response.DirectionsResponse
import kotlinx.coroutines.flow.Flow

interface GoogleRepository {
    suspend fun getDirectionByGoogleApi(origin: String, destination: String, mode: String, apiKey: String) : Flow<DirectionsResponse>
}