package com.example.ai_language.data.source.youtube

import android.util.Log
import com.example.ai_language.data.remote.YoutubeService
import com.example.ai_language.domain.model.response.YoutubeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class YoutubeDataSource @Inject constructor(
    private val youtubeService: YoutubeService
){
    fun getYoutubeData(videoId: String, apiKey: String, part: String = "snippet,statistics") : Flow<Response<YoutubeResponse>> = flow{
        val result = youtubeService.getYoutubeData(videoId, apiKey, part)
        emit(result)
    }.catch {
        Log.e("get By YoutubeData Failure", it.message.toString())
    }
}
