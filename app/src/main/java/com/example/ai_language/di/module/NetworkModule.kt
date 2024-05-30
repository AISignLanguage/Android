package com.example.ai_language.di.module

import com.example.ai_language.Util.Util
import com.example.ai_language.Util.XAccessTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
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

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OpenApiRetrofit3

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TMapRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TranslationRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class YoutubePlayerRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GoogleApiRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class FastApiPlayerRetrofit

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideTokenInterceptor(): XAccessTokenInterceptor {
        return XAccessTokenInterceptor()
    }

    @MogInterceptorOkHttpClient
    @Provides
    @Singleton
    fun provideMogOkHttpClient(
        interceptor: HttpLoggingInterceptor,
        tokenInterceptor: XAccessTokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // 연결 타임아웃 설정 (15초)
            .readTimeout(60, TimeUnit.SECONDS)     // 읽기 타임아웃 설정 (15초)
            .writeTimeout(60, TimeUnit.SECONDS)    // 쓰기 타임아웃 설정 (15초)
            .addInterceptor(interceptor)
            .addInterceptor(tokenInterceptor)
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


    @OpenApiRetrofit3
    @Provides
    @Singleton
    fun provideNaverRetrofit2(
        gsonConverterFactory: GsonConverterFactory,
        @MogInterceptorOkHttpClient client: OkHttpClient  // 이 부분에서도 MogInterceptorOkHttpClient를 사용합니다.
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Util.BASE_URL4)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }


    @TMapRetrofit
    @Provides
    @Singleton
    fun provideTMapRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @MogInterceptorOkHttpClient client: OkHttpClient  // 이 부분에서도 MogInterceptorOkHttpClient를 사용합니다.
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Util.BASE_URL5)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }

    @TranslationRetrofit
    @Provides
    @Singleton
    fun provideTranslationRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @MogInterceptorOkHttpClient client: OkHttpClient  // 이 부분에서도 MogInterceptorOkHttpClient를 사용합니다.
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Util.BASE_URL6)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }

    @YoutubePlayerRetrofit
    @Provides
    @Singleton
    fun provideYoutubePlayerRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @MogInterceptorOkHttpClient client: OkHttpClient  // 이 부분에서도 MogInterceptorOkHttpClient를 사용합니다.
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Util.BASE_URL6)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }

    @FastApiPlayerRetrofit
    @Provides
    @Singleton
    fun provideFastApiRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @MogInterceptorOkHttpClient client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Util.BASE_URL8)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }

    @GoogleApiRetrofit
    @Provides
    @Singleton
    fun provideGoogleApiRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @MogInterceptorOkHttpClient client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Util.BASE_URL9)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }

}
