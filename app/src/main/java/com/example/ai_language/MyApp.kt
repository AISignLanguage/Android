package com.example.ai_language

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    companion object {
        lateinit var application: MyApp

        fun getInstance(): MyApp {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        // 카카오 SDK 초기화
        KakaoSdk.init(this, "ddef2e841959182411eab6098da996e5")

        // 해시키 로깅
        val keyHash = Utility.getKeyHash(this)
        Log.d("해시키", keyHash)

        application = this
    }
}
