package com.example.ai_language.data.source.youtube

import com.example.ai_language.data.source.translation.TranslationDataSource
import com.example.ai_language.domain.model.response.ResultTypeText
import com.example.ai_language.domain.model.response.YoutubeResponse
import com.example.ai_language.domain.repository.YoutubeRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class YoutubeRepositoryImpl @Inject constructor(
    private val youtubeDataSource: YoutubeDataSource
) : YoutubeRepository {

    override suspend fun getYoutubeData(
        videoId: String,
        apiKey: String,
        part: String
    ): Flow<Response<YoutubeResponse>> =
        youtubeDataSource.getYoutubeData(videoId, apiKey, part = "snippet,statistics")

}