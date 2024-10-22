package com.example.ai_language.Util

import androidx.multidex.BuildConfig
import com.example.ai_language.data.remote.Service
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class RetrofitClient private constructor() {

    companion object {
        @Volatile
        private var instance: RetrofitClient? = null
        private const val baseUrl1 = com.example.ai_language.BuildConfig.Main_Server

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
                    .baseUrl(baseUrl1)
                    .addConverterFactory(NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // RxJava 3 Call Adapter Factory 추가
                    .client(retrofitClient.createOkHttpClient())
                    .build()

                retrofit.create(Service::class.java)
            }
        }

    }

    private fun createOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        val tokenInterceptor = XAccessTokenInterceptor()

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addNetworkInterceptor(interceptor)
            .addInterceptor(tokenInterceptor)
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
class NullOnEmptyConverterFactory : Converter.Factory() {
    fun converterFactory() = this
    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
        val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
        override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) {
            try{
                nextResponseBodyConverter.convert(value)
            }catch (e:Exception){
                e.printStackTrace()
                null
            }
        } else{
            null
        }
    }
}
