package com.example.ai_language.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ai_language.R
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.FragmentWalkingBinding
import com.example.ai_language.domain.model.response.Feature
import com.example.ai_language.ui.map.adapter.WalkDirectionAdapter
import com.example.ai_language.ui.map.viewModel.MapViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WalkingFragment : BaseFragment<FragmentWalkingBinding>(R.layout.fragment_walking) {
    private val mapViewModel by activityViewModels<MapViewModel>()
    lateinit var adapter: WalkDirectionAdapter
    private var marker = Marker()
    override fun setLayout() {
        initObservers()
        Log.d("", "")
    }

    private fun formatTime(time: String): String {
        Log.d("시간", time)
        val fTime = time.toInt()
        return if (fTime > 60) {
            if (fTime > 3600) {
                "${fTime / 3600}시간 ${fTime % 60}분"
            } else
                "${fTime / 60}분"
        } else {
            "0분"
        }

    }

    private fun formatTimeDis(time: String) {
        val fTime = time.toInt()
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        binding.tvDepartureTime.text = "출발 시간 :"+LocalDateTime.now().format(timeFormatter)
        if (fTime > 60) {
            if (fTime > 3600) {
                binding.tvArriveTime.text="도착 시간 :"+formatCurrentTimeWithAddedTime((fTime / 3600).toLong(), (fTime / 60).toLong()).replace("오후" , "PM").replace("오전","AM")
            } else
                binding.tvArriveTime.text="도착 시간 :"+formatCurrentTimeWithAddedTime(0, (fTime / 60).toLong()).replace("오후" , "PM").replace("오전","AM")
        } else {
            "0분"
        }

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
    private fun formatDistance(distance: String): String {
        val fDis = distance.toInt()
        return if (fDis > 1000)
            "${fDis / 1000}Km ${fDis % 1000}m"
        else
            "${fDis}m"
    }



    private fun initObservers() {
        adapter = WalkDirectionAdapter()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                mapViewModel.tMapList.collectLatest { response ->
                    val fet = response.features
                    if (response.features.isNotEmpty()) {
                        binding.tvAddress.text =
                            "${mapViewModel.startLoc.value}\n<->\n${mapViewModel.endLoc.value}"
                        val adapterList = mutableListOf<Feature>()
                        val lineList = mutableListOf<Feature>()
                        val pathOverlay = PathOverlay() // 각 단계별로 새 PathOverlay 생성
                        val pathContainer: MutableList<LatLng> = mutableListOf()
                        val totalInfo = fet.find { it.properties.totalDistance > 0 }
                        val time = formatTime(totalInfo?.properties?.totalTime.toString())
                        formatTimeDis(totalInfo?.properties?.totalTime.toString())
                        val distance =
                            formatDistance(totalInfo?.properties?.totalDistance.toString())
                        binding.tvDuration.text = time
                        binding.textView3.text = distance
                        mapViewModel.path.value?.map = null
                        var lineColor = Color.BLACK // 기본 색상
                        var lineIcon: Int = 0
                        for (i in fet.indices) {
                            if (fet[i].geometry.type == "LineString") {
                                lineList.add(fet[i])
                                if (adapterList.size > 0) {
                                    try {
                                        val coordinates = fet[i].geometry.coordinates
                                        if (coordinates is List<*>) {
                                            var mc = false
                                            coordinates.forEach { coordinate ->
                                                if (coordinate is List<*>) {
                                                    val longitude =
                                                        (coordinate[0] as? Double) ?: 0.0
                                                    val latitude = (coordinate[1] as? Double) ?: 0.0
                                                    val point = LatLng(latitude, longitude)
                                                    pathContainer.add(point)
                                                    if (!mc) {
                                                        val desc =
                                                            adapterList[adapterList.size - 1].properties.description
                                                        marker = Marker().apply {
                                                            position = point
                                                            map = mapViewModel.naverMap.value
                                                            //icon = customIcon // 아이콘 설정
                                                            width = Marker.SIZE_AUTO
                                                            height = Marker.SIZE_AUTO
                                                            setOnClickListener {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    desc, Toast.LENGTH_SHORT
                                                                ).show()
                                                                true
                                                            }
                                                        }
                                                        mapViewModel.markers.value?.add(marker)
                                                        mc = true
                                                    }
                                                }
                                            }
                                            if (pathContainer.size >= 2) {
                                                pathOverlay.coords = pathContainer
                                                pathOverlay.color = lineColor
                                                pathOverlay.map = mapViewModel.naverMap.value
                                            }
                                            mapViewModel.pathOverlays.value?.add(pathOverlay)
                                        }

                                        if (fet[i].geometry.type == fet[i - 1].geometry.type) {
                                            fet[i].properties.description =
                                                "${formatDistance(fet[i].properties.distance.toString())} 만큼 이동"
                                            fet[i].properties.time =
                                                formatTime(fet[i].properties.time.toString())
                                            fet[i].properties.distance =
                                                formatDistance(fet[i].properties.distance.toString())
                                            adapterList.add(fet[i])
                                        } else {
                                            adapterList[adapterList.size - 1].properties.time =
                                                formatTime(fet[i].properties.time.toString())
                                            adapterList[adapterList.size - 1].properties.distance =
                                                formatDistance(fet[i].properties.distance.toString())
                                            Log.d(
                                                "내용",
                                                "${adapterList[adapterList.size - 1].properties.time}"
                                            )
                                        }
                                    } catch (e: NumberFormatException) {
                                        Log.e(
                                            "에러",
                                            "${e.message} , ${formatTime(fet[i].properties.time.toString())}"
                                        )
                                        Log.e(
                                            "에러",
                                            "${e.message} , ${formatTime(fet[i].properties.distance.toString())}"
                                        )
                                    }
                                }
                            } else {
                                adapterList.add(fet[i])
                            }

                        }
                        binding.clDetailDirection.visibility = View.VISIBLE
                        binding.tvInfoDis.visibility = View.GONE
                        for (i in 0..<adapterList.size) {
                            Log.d("시간", "${adapterList[i].properties.time}")
                        }
                        adapter.update(adapterList)
                        Log.d("사이즈", "${adapterList.size}")
                        binding.rvDirectionDetail.adapter = adapter
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


    private fun createCustomMarkerIcon(
        context: Context,
        text: String,
        drawable: Int
    ): OverlayImage {
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
}