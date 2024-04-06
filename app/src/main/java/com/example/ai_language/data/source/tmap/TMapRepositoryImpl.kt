package com.example.ai_language.data.source.tmap

import com.example.ai_language.domain.model.request.TmapDTO
import com.example.ai_language.domain.model.response.ApiResponse
import com.example.ai_language.domain.model.response.FeatureCollection
import com.example.ai_language.domain.model.response.ResultPath
import com.example.ai_language.domain.repository.MapRepository
import com.example.ai_language.domain.repository.NaverRepository
import com.example.ai_language.domain.repository.TMapRepository
import dagger.Module
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class TMapRepositoryImpl @Inject constructor(
    private val tMapDataSource: TMapDataSource
) : TMapRepository {
    override suspend fun getRouteBytMapApi(
        tmapDTO: TmapDTO
    ): Flow<FeatureCollection> = tMapDataSource.getRouteBytMapApi(tmapDTO)
}