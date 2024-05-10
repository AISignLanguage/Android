package com.example.ai_language.Util

import android.util.Log
import com.example.ai_language.MyApp
import okhttp3.Interceptor
import okhttp3.Response

class XAccessTokenInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // JWT 토큰 가져오기
        var accessToken =  MyApp.getInstance().tokenManager.accessToken
        var refreshToken = MyApp.getInstance().tokenManager.getJwtRefreshToken()
        var jwtToken = "Bearer $accessToken"

        Log.d("로그", "jwtToken : $jwtToken")

        var request = chain.request()
            .newBuilder()
            .addHeader("Authorization", jwtToken)
            .build()
        val response = chain.proceed(request)

        return response
    }

}