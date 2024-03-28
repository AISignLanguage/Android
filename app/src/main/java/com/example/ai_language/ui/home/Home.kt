package com.example.ai_language.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.ai_language.ui.mypage.MyPage
import com.example.ai_language.ui.news.NewsActivity
import com.example.ai_language.R
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.data.remote.Service
import com.example.ai_language.databinding.ActivityHomeBinding

class Home : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {

    private val CAMERA_PERMISSION_CODE = 1000
    private val READ_CONTACTS_PERMISSION_REQUEST = 1
    lateinit var service: Service


    override fun setLayout() {
//        setButtonClickListeners()
        setBottomNavigation()
    }

    private fun setBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigationMain
        val navController =
            supportFragmentManager.findFragmentById(R.id.container_main)?.findNavController()
        navController?.let {
            bottomNavigationView.setupWithNavController(it)
        }
    }



//    private fun setButtonClickListeners() {
//        findViewById<ImageButton>(R.id.dic_btn).setOnClickListener {
//            startNewActivity(DictionaryPage::class.java)
//        }
//
//        findViewById<ImageButton>(R.id.my_page_btn).setOnClickListener {
//            startNewActivity(MyPage::class.java)
//        }
//
//        findViewById<ImageButton>(R.id.news_btn).setOnClickListener {
//            startNewActivity(NewsActivity::class.java)
//        }
//
//        findViewById<ImageButton>(R.id.callList_btn).setOnClickListener {
//            checkAndRequestPermission(
//                android.Manifest.permission.READ_CONTACTS,
//                READ_CONTACTS_PERMISSION_REQUEST,
//                CallListPage::class.java
//            )
//        }
//
//        findViewById<ImageButton>(R.id.CameraBtn).setOnClickListener {
//            checkAndRequestPermission(
//                android.Manifest.permission.CAMERA,
//                CAMERA_PERMISSION_CODE,
//                com.example.ai_language.ui.camera.com.example.ai_language.ui.camera.CameraPage::class.java
//            )
//        }
//    }
//
//
//    private fun checkAndRequestPermission(permission: String, requestCode: Int, activityClass: Class<*>) {
//        val permissionStatus = ContextCompat.checkSelfPermission(this, permission)
//        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(permission),
//                requestCode
//            )
//        } else {
//            startNewActivity(activityClass)
//        }
//    }

}
