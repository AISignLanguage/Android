package com.example.ai_language.find

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.ai_language.FragmentAdapter
import com.example.ai_language.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FindIdPwd : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_id_pwd)
        initViewPager2()

        if (intent.getBooleanExtra("fromFindEmail", false)) {
            selectTab(1)
        } else {
            selectTab(0)
        }
    }
    private fun initViewPager2() {
        val adapter = FragmentAdapter(this)
        adapter.addFragment(IdFindFragment())
        adapter.addFragment(PasswordFindFragment())

        //어댑터 연결
        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)
        viewPager2.apply {
            viewPager2.adapter = adapter
        }

        //뷰 페이저 와 탭 레이아웃 연결
        val tapLayout = findViewById<TabLayout>(R.id.tab_layout2)
        val tabTitles = listOf("아이디 찾기", "비밀번호 찾기")

        TabLayoutMediator(tapLayout, viewPager2) { tab, position ->
           tab.text = tabTitles[position]
        }.attach()
    }

    private fun selectTab(num: Int) {
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout2)
        tabLayout.getTabAt(num)?.select() // 두 번째 탭 선택
    }

}