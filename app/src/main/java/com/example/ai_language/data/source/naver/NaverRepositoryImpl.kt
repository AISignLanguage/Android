package com.example.ai_language.data.source.naver

import com.example.ai_language.domain.model.response.ApiResponse
import com.example.ai_language.domain.model.response.ResultPath
import com.example.ai_language.domain.repository.MapRepository
import com.example.ai_language.domain.repository.NaverRepository
import dagger.Module
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class NaverRepositoryImpl @Inject constructor(
    private val naverDataSource: NaverDataSource
) : NaverRepository {

    override suspend fun getRouteByOpenApi(
        apiKeyID : String,
        apiKey : String,
        start : String,
        goal : String
    ): Flow<ResultPath> = naverDataSource.getRouteByOpenApi(apiKeyID, apiKey, start, goal)

}