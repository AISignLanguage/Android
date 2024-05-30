package com.example.ai_language.data.source.google

import com.example.ai_language.domain.model.response.ApiResponse
import com.example.ai_language.domain.model.response.DirectionsResponse
import com.example.ai_language.domain.repository.GoogleRepository
import com.example.ai_language.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GoogleRepositoryImpl @Inject constructor(
    private val googleDataSource: GoogleDataSource ,
) : GoogleRepository {
    override suspend fun getDirectionByGoogleApi(
        origin: String, destination: String, mode: String, apiKey: String
    ): Flow<DirectionsResponse> = googleDataSource.getDirectionByGoogleApi(origin, destination, mode, apiKey)
}