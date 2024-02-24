package com.example.ai_language

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "ddef2e841959182411eab6098da996e5")
        // 카카오 SDK 초기화


        val keyHash = Utility.getKeyHash(this)
        Log.d("해시키", keyHash)
    }
}