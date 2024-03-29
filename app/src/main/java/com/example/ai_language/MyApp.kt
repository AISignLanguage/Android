package com.example.ai_language

import android.app.Application
import android.util.Log
import com.example.ai_language.Util.TokenManager
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application() {

    @Inject
    lateinit var tokenManager: TokenManager
    companion object {
        lateinit var application: MyApp

        fun getInstance(): MyApp {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        // 카카오 SDK 초기화
        KakaoSdk.init(this, "ddef2e841959182411eab6098da996e5")
        // 해시키 로깅
        val keyHash = Utility.getKeyHash(this)
        Log.d("해시키", keyHash)

    }
}
