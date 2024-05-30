package com.example.ai_language.domain.repository

import com.example.ai_language.domain.model.response.ResultPath
import kotlinx.coroutines.flow.Flow

interface NaverRepository {
    suspend fun getRouteByOpenApi(apiKeyID : String, apiKey : String, start : String, goal : String): Flow<ResultPath>
}