package com.example.ai_language.domain.repository

import com.example.ai_language.domain.model.request.TmapDTO
import com.example.ai_language.domain.model.response.FeatureCollection
import com.example.ai_language.domain.model.response.ResultPath
import kotlinx.coroutines.flow.Flow

interface TMapRepository {
    suspend fun getRouteBytMapApi(tmapDTO: TmapDTO): Flow<FeatureCollection>
    suspend fun getRouteBytMapDriveApi(tmapDTO: TmapDTO) : Flow<FeatureCollection>
}