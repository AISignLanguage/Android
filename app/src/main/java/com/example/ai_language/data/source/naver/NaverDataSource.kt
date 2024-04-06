package com.example.ai_language.data.source.naver

import android.util.Log
import com.example.ai_language.data.remote.MapService
import com.example.ai_language.data.remote.NaverService
import com.example.ai_language.domain.model.response.ResultPath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NaverDataSource @Inject constructor(
    private val naverService: NaverService
){
    fun getRouteByOpenApi(apiKeyID : String, apiKey : String, start : String, goal : String) : Flow<ResultPath> = flow{
        val result = naverService.getRouteByOpenApi(apiKeyID,apiKey,start,goal)
        emit(result)
    }.catch {
        Log.e("Get Route By OpenApi Failure", it.message.toString())
    }
}