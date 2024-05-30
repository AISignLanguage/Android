package com.example.ai_language.data.source.fastapi

import com.example.ai_language.domain.model.response.WavUrlResponse
import com.example.ai_language.domain.model.response.Wavresponse
import com.example.ai_language.domain.repository.FastApiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FastApiRepositoryImpl @Inject constructor(
    private val fastApiDataSource: FastApiDataSource
) : FastApiRepository {

    override suspend fun getWavUrl(
        youtube_url : WavUrlResponse
    ): Flow<Wavresponse> =
        fastApiDataSource.getWavUrl(youtube_url)

}