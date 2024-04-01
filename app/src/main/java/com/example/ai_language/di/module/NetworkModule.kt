package com.example.ai_language.di.module

import com.example.ai_language.Util.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MogInterceptorOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OpenApiRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OpenApiRetrofit2

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @MogInterceptorOkHttpClient
    @Provides
    @Singleton
    fun provideMogOkHttpClient(
        interceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @MogInterceptorOkHttpClient
    @Provides
    @Singleton
    fun provideMogRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @MogInterceptorOkHttpClient client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Util.BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }

    @OpenApiRetrofit
    @Provides
    @Singleton
    fun provideOpenApiRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @MogInterceptorOkHttpClient client: OkHttpClient  // 이 부분에서도 MogInterceptorOkHttpClient를 사용합니다.
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Util.BASE_URL2)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }

    @OpenApiRetrofit2
    @Provides
    @Singleton
    fun provideOpenApiRetrofit2(
        gsonConverterFactory: GsonConverterFactory,
        @MogInterceptorOkHttpClient client: OkHttpClient  // 이 부분에서도 MogInterceptorOkHttpClient를 사용합니다.
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Util.BASE_URL3)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }
}
