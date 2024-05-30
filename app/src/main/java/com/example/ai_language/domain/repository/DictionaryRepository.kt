package com.example.ai_language.domain.repository

import com.example.ai_language.domain.model.response.ResponseBodys
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {
    suspend fun getDictionaryByOpenApi(serviceKey: String, numOfRows: String, pageNo: String): Flow<ResponseBodys>
}