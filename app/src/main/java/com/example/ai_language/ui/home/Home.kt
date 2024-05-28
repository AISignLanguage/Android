package com.example.ai_language.ui.home


import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ai_language.MyApp
import com.example.ai_language.R
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.databinding.ActivityHomeBinding
import com.example.ai_language.ui.camera.CameraPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Home : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {

    override fun setLayout() {
        setBottomNavigation()
        checkPermissions()
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
    private fun setOnClick(){
        binding.ftbCamera.setOnClickListener{
            startActivity(Intent(this, CameraPage::class.java))

        }
    }

    // 권한 확인 함수
    private fun checkPermissions() {
        // 사용자가 권한  확인 했는지 확인
        val status = ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS")
        if (status == PackageManager.PERMISSION_GRANTED) {
            Log.d("test", "permission granted")
        } else {
            // 권한 확인 다이얼로그 표시
            ActivityCompat.requestPermissions(this, arrayOf<String>("android.permission.READ_CONTACTS"), 100)
            Log.d("test", "permission denied")
        }
    }

}
