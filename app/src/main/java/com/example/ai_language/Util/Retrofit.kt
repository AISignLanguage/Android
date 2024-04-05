package com.example.ai_language.Util

import com.example.ai_language.data.remote.Service
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient private constructor() {

    companion object {
        @Volatile
        private var instance: RetrofitClient? = null
        private const val baseUrl = "http://192.168.219.154:8080/api/mog/userentity/"
        private const val baseUrl1 = "http://34.64.212.107:8080/api/mog/user/"
        private const val baseUrl2 = "http://api.kcisa.kr"

        fun getInstance(): RetrofitClient {
            return instance ?: synchronized(this) {
                instance ?: RetrofitClient().also {
                    instance = it
                }
            }
        }

        fun getUserRetrofitInterface(): Service {
            return getInstance().let { retrofitClient ->
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // RxJava 3 Call Adapter Factory 추가
                    .client(retrofitClient.createOkHttpClient())
                    .build()

                retrofit.create(Service::class.java)
            }
        }

        fun getUserRetrofitInterface2(): Service {
            return getInstance().let { retrofitClient ->
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl2)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(retrofitClient.createOkHttpClient())
                    .build()

                retrofit.create(Service::class.java)
            }
        }
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }
}
