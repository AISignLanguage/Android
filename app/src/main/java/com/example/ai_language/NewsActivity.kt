package com.example.ai_language

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import retrofit2.Call
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Callback
import retrofit2.Response


class FragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragmentList = listOf(TabFragment(), TabFragment2(), TabFragment3())

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}

class NewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val adapter = FragmentAdapter(this)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = adapter

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val tabTitles = listOf("Tab 1", "Tab 2", "Tab 3")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        val back_btn = findViewById<ImageButton>(R.id.back_btn)
        back_btn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }
}