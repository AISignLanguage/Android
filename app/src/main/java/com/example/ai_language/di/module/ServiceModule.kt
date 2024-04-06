package com.example.ai_language.di.module

import com.example.ai_language.data.remote.AccountService
import com.example.ai_language.data.remote.CallListService
import com.example.ai_language.data.remote.DictionaryService
import com.example.ai_language.data.remote.MapService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
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
    fun sendAccountService(
        @NetworkModule.MogInterceptorOkHttpClient retrofit: Retrofit
    ) : AccountService = retrofit.create(AccountService::class.java)
}