package com.example.ai_language.ui.home

import androidx.viewpager2.widget.ViewPager2
import com.example.ai_language.R
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.FragmentHomeBinding
import com.example.ai_language.ui.home.adapter.PagerAdapter


class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val page = 3

    override fun setLayout() {
        setupViewPager()
    }

    private fun setupViewPager() {
        with(binding) {
            val pagerAdapter = PagerAdapter(requireActivity(), page)
            viewpager.adapter = pagerAdapter
            indicator.setViewPager(viewpager)
            indicator.createIndicators(page, 0)
            viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewpager.currentItem = 1000
            viewpager.offscreenPageLimit = 3
            viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    if (positionOffsetPixels == 0) {
                        viewpager.currentItem = position
                    }
                }
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val thispage = position % page;
                    indicator.animatePageSelected(thispage)
                }
            })
        }
    }
}