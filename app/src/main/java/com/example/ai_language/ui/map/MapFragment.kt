package com.example.ai_language.ui.map

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.ai_language.R
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.FragmentMapBinding
import com.example.ai_language.ui.map.adapter.RouteAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map) {
    override fun setLayout() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BaseFragment", "onViewCreated - view created Walking Map")
        setLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("BaseFragment", "onDestroyView - clearing binding Walking Map")
    }

}