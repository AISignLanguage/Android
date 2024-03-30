package com.example.ai_language.ui.home


import android.content.Intent
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ai_language.R
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.data.remote.Service
import com.example.ai_language.databinding.ActivityHomeBinding
import com.example.ai_language.ui.camera.CameraPage


class Home : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {


    override fun setLayout() {
        setBottomNavigation()
        setOnClick()
    }

    private fun setBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigationMain
        val navController =
            supportFragmentManager.findFragmentById(R.id.container_main)?.findNavController()
        navController?.let {
            bottomNavigationView.setupWithNavController(it)
        }

    }
    fun setOnClick(){
        binding.ftbCamera.setOnClickListener{
            startActivity(Intent(this, CameraPage::class.java))

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
