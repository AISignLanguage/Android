package com.example.ai_language.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.ai_language.R
import com.example.ai_language.ui.map.MapFragment as mf
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.databinding.ActivityMapBinding
import com.example.ai_language.domain.model.request.TmapDTO
import com.example.ai_language.ui.map.adapter.RouteAdapter
import com.example.ai_language.ui.map.data.MapDialogData
import com.example.ai_language.ui.map.dialog.CorporationDialog
import com.example.ai_language.ui.map.enums.SettingMapAction
import com.example.ai_language.ui.map.listener.DetailImWriteDialogInterface
import com.example.ai_language.ui.map.viewModel.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapActivity : BaseActivity<ActivityMapBinding>(R.layout.activity_map),
    DetailImWriteDialogInterface, OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource // Naver Map의 위치 정보 소스
    private val mapViewModel by viewModels<MapViewModel>() // ViewModel 인스턴스
    private var dialog: CorporationDialog? = null // 사용자 상호작용을 위한 대화상자
    private lateinit var naverMap: com.naver.maps.map.NaverMap // Naver Map 객체
    private lateinit var fusedLocationClient: FusedLocationProviderClient // Google의 위치 정보 제공 클라이언트
    private lateinit var goal: LatLng
    private var path: PathOverlay? = null
    private val findRouteTab = arrayListOf("대중교통", "자동차", "도보")
    private lateinit var routeAdapter: RouteAdapter
    private var startTitle = ""
    override fun setLayout() {
        Log.d("TransitFragment", "ViewModel instance: ${mapViewModel.hashCode()}")
        initBottomSheet()
        startMap()
        setViewPager()
        setTabLayout()

    }

    private fun setViewPager() {
        routeAdapter = RouteAdapter(this)
        routeAdapter.addFragment(TransitFragment())
        routeAdapter.addFragment(DrivingFragment())
        routeAdapter.addFragment(WalkingFragment())
        binding.vpDirectionRoute.adapter = routeAdapter
        binding.vpDirectionRoute.orientation = ViewPager2.ORIENTATION_HORIZONTAL

    }

    //탭 바 세팅
    private fun setTabLayout() {
        TabLayoutMediator(binding.tbFindRoute, binding.vpDirectionRoute) { tab, position ->
            tab.text = findRouteTab[position]
            when (tab.text) {
                "도보" -> tab.setIcon(R.drawable.walk)
                "대중교통" -> tab.setIcon(R.drawable.transit)
                "자동차" -> tab.setIcon(R.drawable.driving)
            }
        }.attach()

        binding.tbFindRoute.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // 탭이 선택될 때 실행할 코드
                Toast.makeText(this@MapActivity, "${tab.text} 선택됨", Toast.LENGTH_SHORT).show()
                if (mapViewModel.btnState) {
                    when (tab.text) {
                        "대중교통" -> {
                            mapViewModel.clearMap()
                            startDirection(
                                "${mapViewModel.startLatLng.latitude},${mapViewModel.startLatLng.longitude}",
                                "${mapViewModel.endLatLng.latitude},${mapViewModel.endLatLng.longitude}"
                            )
                        }

                        "도보" -> {
                            mapViewModel.clearMap()
                            mapViewModel.getRouteBytMapApi(
                                TmapDTO(
                                    startX = mapViewModel.startLatLng.longitude,
                                    startY = mapViewModel.startLatLng.latitude,
                                    endX = mapViewModel.endLatLng.longitude,
                                    endY = mapViewModel.endLatLng.latitude,
                                    startName = mapViewModel.startLoc.value,
                                    endName = mapViewModel.endLoc.value
                                )
                            )
                        }

                        else -> {
                            mapViewModel.clearMap()
                            mapViewModel.getRouteBytMapDriveApi(
                                TmapDTO(
                                    startX = mapViewModel.startLatLng.longitude,
                                    startY = mapViewModel.startLatLng.latitude,
                                    endX = mapViewModel.endLatLng.longitude,
                                    endY = mapViewModel.endLatLng.latitude,
                                    startName = mapViewModel.startLoc.value,
                                    endName = mapViewModel.endLoc.value
                                )
                            )
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // 탭이 선택 해제될 때 실행할 코드
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // 탭이 다시 선택될 때 실행할 코드
            }
        })

    }
    private fun initBottomSheet() {
        val bottomSheet: View = binding.coordinator
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // 초기 높이 설정
        bottomSheetBehavior.peekHeight = 100  // 첫 시작은 100에서
        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    // 드래깅 도중에는 아무 작업도 수행하지 않음
                } else if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    // 드래그가 끝난 후, 현재 peekHeight를 기반으로 다음 위치 설정
                    bottomSheetBehavior.peekHeight = when (bottomSheetBehavior.peekHeight) {
                        100 -> 600  // 100에서 시작했을 때 위로 드래그하면 600으로 이동
                        600 -> 300  // 600에서 시작했을 때 아래로 드래그하면 300으로 이동
                        300 -> 100  // 300에서 시작했을 때 아래로 드래그하면 100으로 이동
                        else -> bottomSheetBehavior.peekHeight  // 다른 값이 설정되었다면 변경 없음
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 슬라이드 동작에서는 특별한 작업을 수행하지 않음
            }
        })
    }


    //지도 시작
    private fun startMap() {
        getStartLocation() // 시작 위치를 가져오는 메서드를 호출
        initFragment() // MapFragment를 초기화하는 메서드를 호출
        getMapPoint() // 맵 데이터를 가져오는 메서드를 호출
        onClickedByMapResource()
    }

    //시작 위치 설정
    private fun getStartLocation() {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this) // 위치 정보 제공 클라이언트 초기화
        locationSource = FusedLocationSource(this, PERMISSION_REQUEST_CODE) // 위치 정보 소스 초기화
        mapViewModel.setLocationSource(locationSource)
    }

    // MapFragment를 초기화하고 맵이 준비되면 onMapReady 콜백을 받기 위해 호출
    private fun initFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(this) // onMapReady 콜백을 받기 위해 이 메서드를 호출
    }

    //맵 Api가져옴(경기도 수화 통역 센터) (키, 타입, 페이지, 사이즈)
    private fun getMapPoint() {
        mapViewModel.getCorporationByOpenApi("4b7ecc6c16b1492db814d065c2e0e16f", "json", 1, 100)
    }

    //네이버 드라이브 길찾기
    private fun startRoute(start: String, goal: String) {
        mapViewModel.getRouteByOpenApi(
            "wf7lge71wa",
            "S2xrrmnpETvuzVH4QolG12oY050t1GntJ8uxrl23",
            start,
            goal
        )
    }


    //구글 길찾기
    private fun startDirection(or: String, dt: String) {
        // 첫 번째 API 호출
        val transitResponse = mapViewModel.getGoogleDirectionApi(
            origin = or,
            destination = dt,
            mode = "transit",
            apiKey = "AIzaSyCLamg5wXUjHFuF6i_8wka_ZtMCwONPdBY"
        )
    }


    private fun startRoute2(start: LatLng, end: LatLng, startName: String, endName: String) {
        mapViewModel.getRouteBytMapApi(
            TmapDTO(
                startX = start.longitude,
                startY = start.latitude,
                endX = end.longitude,
                endY = end.latitude,
                startName = startName,
                endName = endName
            )
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 사용자가 위치 권한을 부여한 경우, 위치 추적 모드를 Follow로 설정
                mapViewModel.naverMap.value?.locationTrackingMode = LocationTrackingMode.Follow
            } else {
                // 권한이 거부되었다면, 사용자에게 필요성을 알리는 다이얼로그 등을 표시할 수 있습니다.
            }
        }
    }


    //다이얼로그 생성
    private fun addMenu(mapDialogData: MapDialogData) {
        if (dialog?.isVisible != true) {
            dialog = CorporationDialog(this, mapDialogData)
            dialog?.apply {
                isCancelable = true
                showNow(supportFragmentManager, "DetailImWritDialog")
            }
        }
    }

    //다이얼로그 클릭
    override fun onClickButton(id: Int, title : String) {
        when (id) {
            2 -> { //길찾기
                binding.etOrigin.setText("내 위치")
                startTitle = title
                binding.etDestination.setText(title)
                thisLocation(SettingMapAction.LOCATION)
            }

            1 -> {
                // 자세히보기
            }
        }
    }


    //플로팅 버튼 내위치로
    private fun onClickedByMapResource() {
        binding.ftbMapFragmentThisLocation.setOnClickListener {
            Log.d("플로팅버튼", "클릭")
            thisLocation(SettingMapAction.CREATE)
        }
        binding.btSearchRoute.setOnClickListener {
            mapViewModel.btnState = true
            mapViewModel.setStartLoc(binding.etOrigin.text.toString(), LatLng(0.0, 0.0))
            mapViewModel.setEndLoc(binding.etDestination.text.toString(), LatLng(0.0, 0.0))
            startDirection(binding.etOrigin.text.toString(), binding.etDestination.text.toString())
            binding.vpDirectionRoute.setCurrentItem(0, true)
            //길찾기
        }
    }

    //
    private fun thisLocation(setting: SettingMapAction) {
        Log.d("권한", "ㅇ")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("권한", "한개없음")
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // 위치 정보를 사용한 로직
                when (setting) {
                    SettingMapAction.CREATE -> {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        val cameraUpdate = CameraUpdate.scrollTo(currentLatLng)
                        naverMap.moveCamera(cameraUpdate)
                        val marker = Marker().apply { //마커 세팅
                            position = currentLatLng
                            map = naverMap
                        }
                        // 마커를 클릭했을 때 해당 위치의 사업자명을 표시하는 다이얼로그 표시
                        marker.setOnClickListener { overlay ->
                            overlay as Marker
                            val cameraUpdate = CameraUpdate.scrollTo(currentLatLng)
                            naverMap.moveCamera(cameraUpdate) // 마커클릭시 그 위치로 카메라 이동
                            goal = overlay.position
                            true
                        }
                    }

                    SettingMapAction.LOCATION -> {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        val cameraUpdate = CameraUpdate.scrollTo(currentLatLng)
                        naverMap.moveCamera(cameraUpdate)
                        val start = "${currentLatLng.latitude},${currentLatLng.longitude}"
                        mapViewModel.btnState = true
                        mapViewModel.setStartLoc("내 위치", currentLatLng)
                        mapViewModel.setEndLoc(startTitle, LatLng(0.0,0.0))
                        startDirection(start, startTitle)
                        binding.vpDirectionRoute.setCurrentItem(0, true)
                    }
                }
            } else {
                Log.d("MapActivity", "위치 정보가 사용 불가능합니다.")
                // 위치 정보가 없는 경우의 처리 로직
            }
        }
    }

    //다이얼로그 데이터 세팅 (데이터 클래스)
    private fun createDialogData(title: String, address: String, state: String): MapDialogData {
        return MapDialogData(title, address, state)
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onMapReady(naverMap: com.naver.maps.map.NaverMap) {
        this.naverMap = naverMap
        mapViewModel.setNaverMap(naverMap) // ViewModel에 NaverMap 객체 저장        naverMap.locationSource = locationSource
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
        thisLocation(SettingMapAction.CREATE)
        // 여기에서 마커와 다른 지도 관련 설정을 할 수 있습니다.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mapViewModel.corporationList.collectLatest { response ->
                    response.Signlangintrprtcenter.forEach { content ->
                        content.row.forEach { businessInfo ->
                            // 위도, 경도 값이 null이 아닌지 확인하고 안전하게 처리합니다.
                            val latitude = businessInfo.REFINE_WGS84_LAT?.toDoubleOrNull() //위도
                            val longitude =
                                businessInfo.REFINE_WGS84_LOGT?.toDoubleOrNull() //경도
                            if (latitude != null && longitude != null) {
                                val location = LatLng(latitude, longitude) //마커 좌표 설정
                                // 마커 생성 및 설정
                                val marker = Marker().apply { //마커 세팅
                                    position = location
                                    map = naverMap
                                }
                                // 마커를 클릭했을 때 해당 위치의 사업자명을 표시하는 다이얼로그 표시
                                marker.setOnClickListener { overlay ->
                                    overlay as Marker
                                    val cameraUpdate = CameraUpdate.scrollTo(location)
                                    naverMap.moveCamera(cameraUpdate) // 마커클릭시 그 위치로 카메라 이동
                                    // 마커의 위치에 대응하는 사업자명 가져오기
                                    val title = businessInfo.BIZPLC_NM
                                    val address = businessInfo.REFINE_LOTNO_ADDR
                                    val state = businessInfo.BSN_STATE_NM
                                    Log.d("값", "$title + $address + $state")
                                    goal = overlay.position
                                    // 다이얼로그 표시
                                    addMenu(createDialogData(title, address, state))
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


}

