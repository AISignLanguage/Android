package com.example.ai_language.ui.map.viewModel

import com.example.ai_language.domain.model.response.ApiResponse
import android.util.Log
import android.util.LogPrinter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_language.domain.model.request.TmapDTO
import com.example.ai_language.domain.model.response.DirectionsResponse
import com.example.ai_language.domain.model.response.FeatureCollection
import com.example.ai_language.domain.model.response.ResultPath
import com.example.ai_language.domain.model.response.Result_trackoption
import com.example.ai_language.domain.repository.GoogleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.ai_language.domain.repository.MapRepository
import com.example.ai_language.domain.repository.NaverRepository
import com.example.ai_language.domain.repository.TMapRepository
import com.google.type.Color
import com.google.type.LatLng
import com.naver.maps.map.LocationSource
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import java.security.PrivateKey
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val naverRepository: NaverRepository,
    private val tMapRepository: TMapRepository,
    private val googleRepository: GoogleRepository
) : ViewModel() {

    private val _markers = MutableLiveData<MutableList<Marker>>(mutableListOf())
    val markers: LiveData<MutableList<Marker>> = _markers

    private val _pathOverlays = MutableLiveData<MutableList<PathOverlay>>(mutableListOf())
    val pathOverlays: LiveData<MutableList<PathOverlay>> = _pathOverlays

    fun formatTime(time: String): String {
        Log.d("시간", time)
        val fTime = time.toInt()
        return if (fTime > 60) {
            if (fTime > 3600)
                "${fTime / 3600}시간 ${fTime / 60}분"
            else
                "${fTime / 60}분"
        } else {
            "0분"
        }
    }

    fun formatDistance(distance: String): String {
        val fDis = distance.toInt()
        return if(fDis > 1000)
            "${fDis/1000}Km ${fDis%1000}m"
        else
            "${fDis}m"
    }
    fun clearMap() {
        _markers.value?.forEach { it.map = null }
        _markers.value?.clear()
        _pathOverlays.value?.forEach { it.map = null }
        _pathOverlays.value?.clear()

        // LiveData를 업데이트하여 관찰자에게 상태 변경을 알림
        _markers.postValue(_markers.value)
        _pathOverlays.postValue(_pathOverlays.value)
    }

    private val _startLoc = MutableLiveData<String>()
    var startLoc: LiveData<String> = _startLoc

    var startLatLng = com.naver.maps.geometry.LatLng(0.0, 0.0)
    var endLatLng = com.naver.maps.geometry.LatLng(0.0, 0.0)

    fun setStartLoc(value: String, ll : com.naver.maps.geometry.LatLng) {
        _startLoc.value = value
        startLatLng = ll
    }

    private val _endLoc = MutableLiveData<String>()
    var endLoc: LiveData<String> = _endLoc


    fun setEndLoc(value: String,ll : com.naver.maps.geometry.LatLng) {
        _endLoc.value = value
        endLatLng = ll
    }


    val bothLocationsUpdated = MediatorLiveData<Pair<String, String>>().apply {
        var latestStartLoc: String? = null
        var latestEndLoc: String? = null

        addSource(_startLoc) { startLoc ->
            latestStartLoc = startLoc
            latestEndLoc?.let { endLoc ->
                value = Pair(startLoc, endLoc)
            }
        }

        addSource(_endLoc) { endLoc ->
            latestEndLoc = endLoc
            latestStartLoc?.let { startLoc ->
                value = Pair(startLoc, endLoc)
            }
        }
    }


    private var _corporationList = MutableStateFlow(ApiResponse())
    val corporationList: StateFlow<ApiResponse> = _corporationList

    private var _routeList = MutableStateFlow(ResultPath())
    val routeList: StateFlow<ResultPath> = _routeList

    private var _tMapList = MutableStateFlow(FeatureCollection())
    val tMapList: StateFlow<FeatureCollection> = _tMapList


    private var _directionList = MutableStateFlow(DirectionsResponse())
    val directionList: StateFlow<DirectionsResponse> = _directionList

    private var _directionList2 = MutableStateFlow(DirectionsResponse())
    val directionList2: StateFlow<DirectionsResponse> = _directionList2

    private var _directionList3 = MutableStateFlow(DirectionsResponse())
    val directionList3: StateFlow<DirectionsResponse> = _directionList3

    private val _naverMap = MutableLiveData<NaverMap>()
    val naverMap: LiveData<NaverMap> = _naverMap

    fun setNaverMap(map: com.naver.maps.map.NaverMap) {
        _naverMap.value = map
    }

    private var _path = MutableLiveData<PathOverlay>()
    val path: LiveData<PathOverlay> = _path

    fun setPath(path: PathOverlay) {
        _path.value = path
    }

    private var _locationSource = MutableLiveData<FusedLocationSource>()
    val locationSource: LiveData<FusedLocationSource> = _locationSource // Naver Map의 위치 정보 소스

    fun setLocationSource(locationSource: FusedLocationSource) {
        _locationSource.value = locationSource
    }


    fun getCorporationByOpenApi(KEY: String, Type: String, pIndex: Int, pSize: Int) {
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


    fun getRouteByOpenApi(apiKeyID: String, apiKey: String, start: String, goal: String) {
        viewModelScope.launch {
            try {
                naverRepository.getRouteByOpenApi(apiKeyID, apiKey, start, goal).collect {
                    _routeList.value = it
                }
            } catch (e: Exception) {
                Log.e("Map Error", e.message.toString())
            }
        }
    }

    fun getRouteBytMapApi(tmapDTO: TmapDTO) {
        viewModelScope.launch {
            try {
                tMapRepository.getRouteBytMapApi(tmapDTO).collect {
                    _tMapList.value = it
                }
            } catch (e: Exception) {
                Log.e("TMap Error", e.message.toString())
            }
        }
    }

    fun getGoogleDirectionApi(origin: String, destination: String, mode: String, apiKey: String) {
        viewModelScope.launch {
            try {
                googleRepository.getDirectionByGoogleApi(origin, destination, mode, apiKey)
                    .collect {
                        _directionList.value = it
                    }
            } catch (e: Exception) {
                Log.e("Google Error", e.message.toString())
            }
        }
    }

    fun getGoogleDirectionApi2(origin: String, destination: String, mode: String, apiKey: String) {
        viewModelScope.launch {
            try {
                googleRepository.getDirectionByGoogleApi(origin, destination, mode, apiKey)
                    .collect {
                        _directionList2.value = it
                    }
            } catch (e: Exception) {
                Log.e("Google Error", e.message.toString())
            }
        }
    }

    fun getGoogleDirectionApi3(origin: String, destination: String, mode: String, apiKey: String) {
        viewModelScope.launch {
            try {
                googleRepository.getDirectionByGoogleApi(origin, destination, mode, apiKey)
                    .collect {
                        _directionList3.value = it
                    }
            } catch (e: Exception) {
                Log.e("Google Error", e.message.toString())
            }
        }
    }

}