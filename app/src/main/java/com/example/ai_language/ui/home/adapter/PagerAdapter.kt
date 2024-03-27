package com.example.ai_language.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ai_language.ui.home.Page1
import com.example.ai_language.ui.home.Page2
import com.example.ai_language.ui.home.Page3


class PagerAdapter(fa: FragmentActivity, private val mCount: Int) : FragmentStateAdapter(fa) {

    override fun createFragment(position: Int): Fragment {

        return when (getRealPosition(position)) {
            0 -> Page1()
            1 -> Page2()
            else -> Page3()
        }
    }

    override fun getItemCount(): Int {
        return 2000
    }

    private fun getRealPosition(position: Int): Int {
        return position % mCount
    }
}

