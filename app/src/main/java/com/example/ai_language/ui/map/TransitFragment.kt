package com.example.ai_language.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ai_language.R
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.FragmentTransitBinding
import com.example.ai_language.domain.model.response.Leg
import com.example.ai_language.domain.model.response.Location
import com.example.ai_language.ui.map.adapter.DirectionDetailAdapter
import com.example.ai_language.ui.map.data.DirectionInfoData
import com.example.ai_language.ui.map.viewModel.MapViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class TransitFragment : BaseFragment<FragmentTransitBinding>(R.layout.fragment_transit) {
    private val mapViewModel by activityViewModels<MapViewModel>()
    lateinit var adapter: DirectionDetailAdapter
    private var marker = Marker()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TransitFragment", "ViewModel instance: ${mapViewModel.hashCode()}")
        Log.d("TransitFragment", "Initializing TransitFragment views.")
        initObservers()
    }

    override fun setLayout() {
        Log.d("TransitFragment", "Initializing TransitFragment views.")
    }


    private fun formatCurrentTimeWithAddedTime(hour: Long, min: Long): String {
        // 시간 포맷터 생성
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        // 현재 시각 가져오기
        val currentTime = LocalDateTime.now()
        // 시간 추가
        val newTime = currentTime.plusHours(hour).plusMinutes(min)
        // 포맷된 시간 문자열 반환
        return newTime.format(timeFormatter)
    }
    private fun initObservers() {
        adapter = DirectionDetailAdapter()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                mapViewModel.directionList.collectLatest { response ->
                    if (response.routes.isNotEmpty() && response.routes[0].legs.isNotEmpty()) {
                        mapViewModel.clearMap()
                        val route = response.routes[0].legs[0]
                        mapViewModel.path.value?.map = null
                        val steps = route.steps
                        steps.forEach { step ->
                            val pathOverlay = PathOverlay() // 각 단계별로 새 PathOverlay 생성
                            val pathContainer: MutableList<LatLng> = mutableListOf()
                            var lineColor = Color.BLACK // 기본 색상
                            var lineIcon: Int = 0
                            with(step) {
                                when (travel_mode) {
                                    "WALKING" -> {
                                        step.travel_mode = "도보"
                                        lineColor = Color.GREEN
                                        lineIcon = R.drawable.walk // 아이콘 설정
                                    }

                                    "TRANSIT" -> {
                                        step.travel_mode = "대중교통"
                                        lineColor = Color.BLUE
                                        lineIcon = R.drawable.transit // 아이콘 설정

                                    }

                                    else -> {
                                        step.travel_mode = "자동차"
                                        lineColor = Color.RED
                                        lineIcon = R.drawable.driving // 아이콘 설정
                                    }
                                }
                                val startLongitude = start_location.lng
                                val startLatitude = start_location.lat
                                val endLongitude = end_location.lng
                                val endLatitude = end_location.lat
                                val startPoint = LatLng(startLatitude, startLongitude)
                                val endPoint = LatLng(endLatitude, endLongitude)
                                pathContainer.add(startPoint)
                                pathContainer.add(endPoint)
                                if (pathContainer.size >= 2) {
                                    pathOverlay.coords = pathContainer
                                    pathOverlay.color = lineColor
                                    pathOverlay.map = mapViewModel.naverMap.value // 각 PathOverlay를 지도에 적용
                                }
                                val customIcon = createCustomMarkerIcon(
                                    requireContext(),
                                    step.html_instructions,
                                    lineIcon
                                )
                                marker = Marker().apply {
                                    position = startPoint
                                    map = mapViewModel.naverMap.value
                                    icon = customIcon // 아이콘 설정
                                    width = Marker.SIZE_AUTO
                                    height = Marker.SIZE_AUTO
                                }
                                mapViewModel.markers.value?.add(marker)
                            }
                            mapViewModel.pathOverlays.value?.add(pathOverlay)
                        }
                        val cameraUpdate = CameraUpdate.scrollTo(
                            LatLng(
                                route.start_location.lat,
                                route.start_location.lng
                            )
                        )
                            .animate(CameraAnimation.Fly, 3000)
                        mapViewModel.naverMap.value?.moveCamera(cameraUpdate)

                        Log.d("TransitFragment", "routes available")
                        updateUI(route)
                        adapter.update(route.steps)
                        binding.rvDirectionDetail.adapter = adapter
                        Log.d("리스트", adapter.itemCount.toString())
                        val lls = response.routes[0].legs[0].start_location
                        val lle = response.routes[0].legs[0].end_location
                        mapViewModel.setStartLoc(mapViewModel.startLoc.value.toString(),LatLng(lls.lat,lls.lng))
                        mapViewModel.setEndLoc(mapViewModel.endLoc.value.toString(),LatLng(lle.lat,lle.lng))
                    } else {
                        binding.clDetailDirection.visibility = View.GONE
                        binding.tvInfoDis.visibility = View.VISIBLE
                        Log.d("TransitFragment", "No routes available")
                        // Handle empty routes scenario, possibly show an empty state to the user
                    }
                }
            }
        }

    }

    private fun createCustomMarkerIcon(context: Context, text: String, drawable: Int): OverlayImage {
        // 원본 이미지 로드
        val drawable = ContextCompat.getDrawable(context, drawable)
            ?: return OverlayImage.fromBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
        val bitmapDrawable = drawable.toBitmap()

        // 텍스트 크기 및 스타일 설정
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK // 텍스트 색상 설정
            textSize = 40f       // 텍스트 크기 설정
            typeface = Typeface.DEFAULT_BOLD
        }

        // 텍스트 크기 측정
        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)

        // 새로운 비트맵 크기 계산 (텍스트 배경 포함)
        val padding = 20 // 텍스트 주변 패딩
        val bgWidth = textBounds.width() + padding * 2
        val bgHeight = textBounds.height() + padding

        // 새로운 비트맵 생성 (이미지 + 텍스트 공간)
        val combinedBitmap = Bitmap.createBitmap(
            bitmapDrawable.width + bgWidth,
            bitmapDrawable.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(combinedBitmap)

        // 이미지 그리기
        canvas.drawBitmap(bitmapDrawable, 0f, 0f, null)

        // 텍스트 배경 그리기
        val backgroundPaint = Paint().apply {
            color = Color.WHITE // 배경 색상
            style = Paint.Style.FILL
        }
        val bgLeft = bitmapDrawable.width.toFloat()
        val bgTop = bitmapDrawable.height / 2f - bgHeight / 2f
        canvas.drawRect(bgLeft, bgTop, bgLeft + bgWidth, bgTop + bgHeight, backgroundPaint)

        // 텍스트 그리기 (이미지 오른쪽에 위치)
        val x = bgLeft + padding
        val y = bitmapDrawable.height / 2f + textBounds.height() / 2f
        canvas.drawText(text, x, y, paint)

        return OverlayImage.fromBitmap(combinedBitmap)
    }

    private fun updateUI(route: Leg) {
        binding.clDetailDirection.visibility = View.VISIBLE
        binding.tvInfoDis.visibility = View.GONE
        val routeDataBindingItem = DirectionInfoData(
            route.arrival_time.text,
            route.departure_time.text,
            route.distance.text,
            route.duration.text,
            mapViewModel.startLoc.value.toString(),
            mapViewModel.endLoc.value.toString(),
            Location(0.0, 0.0),
            Location(0.0, 0.0)
        )
        binding.directionInfoData = routeDataBindingItem
        binding.invalidateAll()
    }

}
