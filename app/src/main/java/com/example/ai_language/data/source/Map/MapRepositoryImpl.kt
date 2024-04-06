package com.example.ai_language.data.source.Map

import com.example.ai_language.domain.model.response.ApiResponse
import com.example.ai_language.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class MapRepositoryImpl @Inject constructor(
    private val mapDataSource: MapDataSource,
) : MapRepository {
    override suspend fun getCorporationByOpenApi(
        KEY: String,
        Type: String,
        pIndex: Int,
        pSize: Int
    ): Flow<ApiResponse> = mapDataSource.getCorporationByOpenApi(KEY, Type, pIndex, pSize)
}