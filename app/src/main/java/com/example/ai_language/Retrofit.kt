package com.example.ai_language

import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient private constructor() {

    companion object {
        @Volatile
        private var instance: RetrofitClient? = null
        private lateinit var userRetrofitInterface: Service
        private val baseUrl = "https://c736-2001-e60-879e-8682-dc6c-e223-5d7b-4b74.ngrok-free.app/"

        fun getInstance(): RetrofitClient {
            return instance ?: synchronized(this) {
                instance ?: RetrofitClient().also { instance = it }
            }
        }

        fun getUserRetrofitInterface(): Service {
            return userRetrofitInterface
        }
    }

    init {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃 설정
            .readTimeout(30, TimeUnit.SECONDS)    // 읽기 타임아웃 설정
            .writeTimeout(30, TimeUnit.SECONDS)   // 쓰기 타임아웃 설정
            .protocols(listOf(Protocol.HTTP_1_1))
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        userRetrofitInterface = retrofit.create(Service::class.java)
    }
}
