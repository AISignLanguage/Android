package com.example.ai_language.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.ai_language.R
import com.example.ai_language.databinding.BottomDialogBinding
import com.example.ai_language.domain.model.request.TmapDTO
import com.example.ai_language.ui.map.adapter.RouteAdapter
import com.example.ai_language.ui.map.viewModel.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BottomCalendar : BottomSheetDialogFragment() {
    private var _binding: BottomDialogBinding? = null
    private val binding get() = _binding!!
    private val findRouteTab = arrayListOf("대중교통", "자동차", "도보")
    private lateinit var routeAdapter: RouteAdapter
    private val mapViewModel by viewModels<MapViewModel>() // ViewModel 인스턴스

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bottom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewPager()
        setTabLayout()

        // 팝업의 BottomSheet을 가져옵니다.
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)

        // 사용자가 드래그를 시작하면, 드래그 위치에 따라 BottomSheet의 상태를 설정합니다.
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // STATE_DRAGGING 상태를 변경 없이 그대로 사용
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // onSlide는 드래그 중 슬라이드의 변화를 관찰할 수 있습니다.
            }
        })
    }

    private fun setViewPager() {
        routeAdapter = RouteAdapter(requireActivity())
        routeAdapter.addFragment(TransitFragment())
        routeAdapter.addFragment(DrivingFragment())
        routeAdapter.addFragment(WalkingFragment())
        binding.vpDirectionRoute.adapter = routeAdapter
        binding.vpDirectionRoute.orientation = ViewPager2.ORIENTATION_HORIZONTAL

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
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
                Toast.makeText(requireContext(), "${tab.text} 선택됨", Toast.LENGTH_SHORT).show()
                if(mapViewModel.btnState) {
                    when (tab.text) {
                        "대중교통"-> {

                            binding.vpDirectionRoute.setCurrentItem(0,true)
                            mapViewModel.clearMap()
//                            startDirection(
//                                binding.etOrigin.text.toString(),
//                                binding.etDestination.text.toString() //view모델 옵저버로
//                            )
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

}
