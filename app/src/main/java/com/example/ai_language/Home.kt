package com.example.ai_language

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import me.relex.circleindicator.CircleIndicator3
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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


class Home : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 1000
    private val READ_CONTACTS_PERMISSION_REQUEST = 1

    lateinit var call: Call<CallListDTO>
    lateinit var service: Service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val page = 3
        val viewPager2 = findViewById<ViewPager2>(R.id.viewpager)
        val pagerAdapter = PagerAdapter(this,page)
        viewPager2.adapter = pagerAdapter

        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        indicator.setViewPager(viewPager2)
        indicator.createIndicators(page,0)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        viewPager2.currentItem = 1000
        viewPager2.offscreenPageLimit = 3

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffsetPixels == 0) {
                    viewPager2.currentItem = position
                }
            }
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val thispage = position % page;
                indicator.animatePageSelected(thispage)

            }


        })


        val dic_btn = findViewById<ImageButton>(R.id.dic_btn)
        dic_btn.setOnClickListener {
            val intent = Intent(this,DictionaryPage::class.java)
            startActivity(intent)
        }

        val mypg_btn = findViewById<ImageButton>(R.id.my_page_btn)
        mypg_btn.setOnClickListener{
            val intent = Intent(this,MyPage::class.java)
            startActivity(intent)
        }

        val news_btn = findViewById<ImageButton>(R.id.news_btn)
        news_btn.setOnClickListener {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }

        val callLsit_btn = findViewById<ImageButton>(R.id.callList_btn) //전화번호부 화면으로 이동

        callLsit_btn.setOnClickListener {
            val callPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
            if(callPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_CONTACTS),
                    READ_CONTACTS_PERMISSION_REQUEST
                )
            }
            else{
                val intent = Intent(this, CallListPage::class.java)
                startActivity(intent)
            }
        }

        val camera_btn = findViewById<ImageButton>(R.id.CameraBtn) //카메라(동영상) 화면으로 이동
        camera_btn.setOnClickListener{
            val cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
            else{
                val intent = Intent(this, CameraPage::class.java)
                startActivity(intent)
            }
        }
    }
}