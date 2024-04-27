package com.example.ai_language.data.source.fastapi

import android.util.Log
import com.example.ai_language.data.remote.FastApiService
import com.example.ai_language.domain.model.response.WavUrlResponse
import com.example.ai_language.domain.model.response.Wavresponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FastApiDataSource @Inject constructor(
    private val fastApiService: FastApiService
){
    fun getWavUrl(youtube_url : WavUrlResponse) : Flow<Wavresponse> = flow{
        val result = fastApiService.getWavUrl(youtube_url)
        emit(result)
    }.catch {
        Log.e("get By FastApi Data Failure", it.message.toString())
    }
}
