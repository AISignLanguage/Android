package com.example.ai_language.ui.map.viewModel

import com.example.ai_language.domain.model.response.ApiResponse
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.ai_language.domain.repository.MapRepository
import javax.inject.Inject


@HiltViewModel
class MapViewModel  @Inject constructor(
    private val mapRepository: MapRepository
) : ViewModel(){

    private var _corporationList = MutableStateFlow(ApiResponse())
    val corporationList: StateFlow<ApiResponse> = _corporationList

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

}