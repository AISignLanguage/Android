package com.example.ai_language.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.example.ai_language.R
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.databinding.ActivityMapBinding
import com.example.ai_language.databinding.DialogBusinessInfoBinding
import com.example.ai_language.ui.dictionary.viewmodel.DictionaryViewModel
import com.example.ai_language.ui.map.viewModel.MapViewModel
import com.naver.maps.map.CameraUpdate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapActivity : BaseActivity<ActivityMapBinding>(R.layout.activity_map), OnMapReadyCallback {

    private val mapViewModel by viewModels<MapViewModel>()

    override fun setLayout() {
        initFragment()
        startMapPoint()
    }

    private fun initFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(this) // onMapReady 콜백을 받기 위해 이 메서드를 호출합니다.

    }

    private fun startMapPoint() {
        mapViewModel.getCorporationByOpenApi("4b7ecc6c16b1492db814d065c2e0e16f", "json", 1, 100)
    }

    override fun onMapReady(naverMap: com.naver.maps.map.NaverMap) {
        // 여기에서 마커와 다른 지도 관련 설정을 할 수 있습니다.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mapViewModel.corporationList.collectLatest { response ->
                    response.Signlangintrprtcenter.forEach { content ->
                        content.row.forEach { businessInfo ->
                            // 위도, 경도 값이 null이 아닌지 확인하고 안전하게 처리합니다.
                            val latitude = businessInfo.REFINE_WGS84_LAT?.toDoubleOrNull()
                            val longitude = businessInfo.REFINE_WGS84_LOGT?.toDoubleOrNull()
                            if (latitude != null && longitude != null) {
                                val location = LatLng(latitude, longitude)
                                // 마커 생성 및 설정
                                val marker = Marker().apply {
                                    position = location
                                    map = naverMap
                                }
                                // 마커를 클릭했을 때 해당 위치의 사업자명을 표시하는 다이얼로그 표시
                                marker.setOnClickListener { overlay ->
                                    overlay as Marker
                                    // 마커의 위치에 대응하는 사업자명 가져오기
                                    val businessName = businessInfo.BIZPLC_NM
                                    val address = businessInfo.REFINE_LOTNO_ADDR
                                    // 다이얼로그 표시
                                    showBusinessInfoDialog(businessName, address)
                                    true
                                }
                            } else {
                                // 위도, 경도 값이 null이거나 변환할 수 없는 경우에 대한 처리
                                Log.e(
                                    "MapActivity",
                                    "Invalid latitude or longitude value: $latitude, $longitude"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showBusinessInfoDialog(businessName: String, address: String) {
        // 다이얼로그 레이아웃을 인플레이트하여 뷰 바인딩 생성
        val dialogBinding = DialogBusinessInfoBinding.inflate(LayoutInflater.from(this))

        // 다이얼로그에 표시할 정보 설정
        dialogBinding.textViewBusinessName.text = businessName
        dialogBinding.textViewAddress.text = address

        // 다이얼로그 생성
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("확인") { _, _ ->
                // 확인 버튼 클릭 시 처리할 내용 (이 경우 아무 작업 없음)
            }
            .create()
        // 다이얼로그 표시
        dialog.show()
    }
}