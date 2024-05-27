package com.example.ai_language.ui.camera


import android.util.Log
import android.webkit.WebViewClient
import com.example.ai_language.BuildConfig
import com.example.ai_language.R
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.databinding.ActivityVideoBinding

class VideoActivity : BaseActivity<ActivityVideoBinding>(R.layout.activity_video) {
    override fun setLayout() {
        binding.webview.webViewClient = WebViewClient()  // 클릭시 새 창이 뜨지 않도록
        // 자바스크립트 허용
        binding.webview.settings.javaScriptEnabled = true
        // WebView에서 복잡한 URL 처리 가능하도록 설정
        binding.webview.settings.domStorageEnabled = true
        initButton()
        // 로컬 네트워크의 서버 주소와 포트 번호, 방 번호를 확인하세요
    }

    private fun initButton() {
        binding.btnInputLink.setOnClickListener {
            val serverUrl = binding.editText2.text.toString()
            binding.webview.loadUrl(BuildConfig.Main_Server_5000 + "video_feed/" + serverUrl)
            Log.d("링크", "${BuildConfig.Main_Server + "video_feed/" + serverUrl}")
        }
    }
}