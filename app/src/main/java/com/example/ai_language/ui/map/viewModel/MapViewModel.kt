package com.example.ai_language.ui.map.viewModel

import com.example.ai_language.domain.model.response.ApiResponse
import android.util.Log
import android.util.LogPrinter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_language.domain.model.request.TmapDTO
import com.example.ai_language.domain.model.response.FeatureCollection
import com.example.ai_language.domain.model.response.ResultPath
import com.example.ai_language.domain.model.response.Result_trackoption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.ai_language.domain.repository.MapRepository
import com.example.ai_language.domain.repository.NaverRepository
import com.example.ai_language.domain.repository.TMapRepository
import java.security.PrivateKey
import javax.inject.Inject


@HiltViewModel
class MapViewModel  @Inject constructor(
    private val mapRepository: MapRepository,
    private val naverRepository: NaverRepository,
    private val tMapRepository: TMapRepository
) : ViewModel(){

    private var _corporationList = MutableStateFlow(ApiResponse())
    val corporationList: StateFlow<ApiResponse> = _corporationList

    private var _routeList = MutableStateFlow(ResultPath())
    val routeList : StateFlow<ResultPath> = _routeList

    private var _tMapList = MutableStateFlow(FeatureCollection())
    val tMapList : StateFlow<FeatureCollection> = _tMapList

    fun getCorporationByOpenApi(KEY: String, Type: String, pIndex: Int, pSize : Int) {
        viewModelScope.launch {
            try {
                mapRepository.getCorporationByOpenApi(KEY, Type, pIndex, pSize).collect {
                    _corporationList.value = it
                }
            } catch (e: Exception) {
                Log.e("Map Error", e.message.toString())
            }
        }
    }

    fun getRouteByOpenApi( apiKeyID : String, apiKey : String, start : String, goal : String){
        viewModelScope.launch {
            try {
                naverRepository.getRouteByOpenApi(apiKeyID, apiKey, start, goal).collect {
                    _routeList.value = it
                }
            }catch (e: Exception){
                Log.e("Map Error", e.message.toString())
            }
        }
    }

    fun getRouteBytMapApi(tmapDTO: TmapDTO){
        viewModelScope.launch {
            try {
                tMapRepository.getRouteBytMapApi(tmapDTO).collect{
                    _tMapList.value = it
                }
            }catch(e : Exception){
                Log.e("TMap Error", e.message.toString())
            }
        }
    }

}