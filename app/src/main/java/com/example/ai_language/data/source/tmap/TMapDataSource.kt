package com.example.ai_language.data.source.tmap

import android.util.Log
import com.example.ai_language.data.remote.MapService
import com.example.ai_language.data.remote.NaverService
import com.example.ai_language.data.remote.TmapService
import com.example.ai_language.domain.model.request.TmapDTO
import com.example.ai_language.domain.model.response.FeatureCollection
import com.example.ai_language.domain.model.response.ResultPath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TMapDataSource @Inject constructor(
    private val tmapService: TmapService
){
    fun getRouteBytMapApi(tmapDTO: TmapDTO) : Flow<FeatureCollection> = flow{
        val result = tmapService.getRouteBytMapApi(tmapDTO)
        emit(result)
    }.catch {
        Log.e("Get Route By OpenApi Failure", it.message.toString())
    }
}