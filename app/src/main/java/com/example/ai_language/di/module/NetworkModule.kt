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

    @MogInterceptorOkHttpClient
    @Singleton
    @Provides
    fun provideMogOkHttpClient(
        interceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @MogInterceptorOkHttpClient
    @Singleton
    @Provides
    fun provideMogRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        @MogInterceptorOkHttpClient client: OkHttpClient
    ) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(Util.BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()
}