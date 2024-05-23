package com.example.ai_language.ui.camera


import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_language.R
import com.example.ai_language.base.BaseActivity
import com.example.ai_language.databinding.ActivityVideoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

class VideoActivity : BaseActivity<ActivityVideoBinding>(R.layout.activity_video) {

    override fun setLayout() {
        binding.webview.webViewClient = WebViewClient()  // 클릭시 새 창이 뜨지 않도록

        // 자바스크립트 허용
        binding.webview.settings.javaScriptEnabled = true

        // WebView에서 복잡한 URL 처리 가능하도록 설정
        binding.webview.settings.domStorageEnabled = true

        // 로컬 네트워크의 서버 주소와 포트 번호, 방 번호를 확인하세요
        val serverUrl = "http://34.64.202.194:5000/"
        binding.webview.loadUrl(serverUrl)
    }

}