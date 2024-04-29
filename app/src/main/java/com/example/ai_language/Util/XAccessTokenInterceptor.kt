package com.example.ai_language.Util

import com.example.ai_language.MyApp
import okhttp3.Interceptor
import okhttp3.Response

class XAccessTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // JWT 토큰 가져오기
        var accessToken = MyApp.getInstance().tokenManager.getJwtAccessToken()
        var refreshToken = MyApp.getInstance().tokenManager.getJwtRefreshToken()

        var request = chain.request()
            .newBuilder()
            .addHeader("JWT", "$accessToken")
            .build()
        val response = chain.proceed(request)

        return response
    }

}