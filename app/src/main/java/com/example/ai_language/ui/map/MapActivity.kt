package com.example.ai_language.ui.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.example.ai_language.R
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.databinding.ActivityMapBinding
import com.naver.maps.map.CameraUpdate

class MapActivity : BaseActivity<ActivityMapBinding>(R.layout.activity_map), OnMapReadyCallback {
    override fun setLayout() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(this) // onMapReady 콜백을 받기 위해 이 메서드를 호출합니다.

    }

    override fun onMapReady(naverMap: com.naver.maps.map.NaverMap) {
        // 여기에서 마커와 다른 지도 관련 설정을 할 수 있습니다.

        // 마커 위치 설정
        val location = LatLng(37.5666805, 126.9784147) // 서울시청의 위도와 경도

        // 마커 생성 및 설정
        Marker().apply {
            position = location
            map = naverMap
        }

        // 필요하다면, 카메라 위치를 마커 위치로 이동시킬 수 있습니다.
        naverMap.moveCamera(CameraUpdate.scrollTo(location))
    }
}
