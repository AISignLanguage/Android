package com.example.ai_language.data.source.Map

import android.util.Log
import com.example.ai_language.data.remote.MapService
import com.example.ai_language.domain.model.response.ApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MapDataSource @Inject constructor(
    private val mapService: MapService
) {
    fun getCorporationByOpenApi(KEY: String, Type: String, pIndex: Int, pSize : Int) : Flow<ApiResponse> = flow{
        val result =mapService.getCorporationByOpenApi(KEY,Type,pIndex,pSize)
        emit(result)
    }.catch {
        Log.e("Get Dictionary By OpenApi Failure", it.message.toString())
    }
}