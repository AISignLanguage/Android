package com.example.ai_language.di.module

import com.example.ai_language.data.remote.DictionaryService
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


}