package com.example.ai_language.ui.map.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class RouteAdapter (fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    private val fragmentList : ArrayList<Fragment> = ArrayList()

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList[position]

    fun addFragment(fragment: Fragment){
        fragmentList.add(fragment)
        notifyItemInserted(fragmentList.size -1)
    }

}
