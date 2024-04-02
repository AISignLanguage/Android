package com.example.ai_language.domain.repository

import com.example.ai_language.domain.model.response.ApiResponse
import com.example.ai_language.domain.model.response.ResponseBodys
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    suspend fun getCorporationByOpenApi(KEY: String, Type: String, pIndex: Int, pSize : Int): Flow<ApiResponse>

}