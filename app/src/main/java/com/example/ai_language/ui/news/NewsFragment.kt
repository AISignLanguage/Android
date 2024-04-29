package com.example.ai_language.ui.news

import android.content.Intent
import com.example.ai_language.R
import com.example.ai_language.Util.FragmentAdapter
import com.example.ai_language.base.BaseFragment
import com.example.ai_language.databinding.ActivityNewsBinding
import com.example.ai_language.ui.home.Home
import com.google.android.material.tabs.TabLayoutMediator

class NewsFragment : BaseFragment<ActivityNewsBinding>(R.layout.activity_news) {
    override fun setLayout() {
        with(binding) {
            val adapter = FragmentAdapter(requireActivity())
            adapter.addFragment(TabFragment())
            adapter.addFragment(TabFragment2())
            adapter.addFragment(TabFragment3())

            viewPager.adapter = adapter

            val tabTitles = listOf("자음", "모음", "숫자")

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabTitles[position]
            }.attach()

//            backBtn.setOnClickListener {
//                val intent = Intent(requireActivity(), Home::class.java)
//                startActivity(intent)
//                requireActivity().finish()
//            }
        }
    }
}