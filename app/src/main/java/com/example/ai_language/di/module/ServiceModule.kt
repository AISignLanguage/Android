package com.example.ai_language.di.module

import com.example.ai_language.data.remote.CallListService
import com.example.ai_language.data.remote.DictionaryService
import com.example.ai_language.data.remote.MapService
import com.example.ai_language.data.remote.NaverService
import com.example.ai_language.data.remote.TmapService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideOpenApiService(
        @NetworkModule.OpenApiRetrofit retrofit: Retrofit
    ): DictionaryService = retrofit.create(DictionaryService::class.java)

    @Provides
    @Singleton
    fun sendCallService(
        @NetworkModule.MogInterceptorOkHttpClient retrofit: Retrofit
    ) : CallListService = retrofit.create(CallListService::class.java)

    @Provides
    @Singleton
    fun sendCorporationService(
        @NetworkModule.OpenApiRetrofit2 retrofit: Retrofit
    ) : MapService = retrofit.create(MapService::class.java)


    @Provides
    @Singleton
    fun sendNaverService(
        @NetworkModule.OpenApiRetrofit3 retrofit: Retrofit
    ) : NaverService = retrofit.create(NaverService::class.java)

    @Provides
    @Singleton
    fun sendTMapService(
        @NetworkModule.TMapRetrofit retrofit: Retrofit
    ) : TmapService = retrofit.create(TmapService::class.java)
}


