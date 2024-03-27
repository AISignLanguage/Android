//package com.example.ai_language.di.module
//
//import com.goorm.kkiri.data.remote.BoardService
//import com.goorm.kkiri.data.remote.ChatService
//import com.goorm.kkiri.data.remote.MemberService
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import retrofit2.Retrofit
//import retrofit2.create
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object ServiceModule {
//
//    @Provides
//    @Singleton
//    fun provideMemberService(
//        @NetworkModule.MogInterceptorOkHttpClient retrofit: Retrofit
//    ): MemberService = retrofit.create(MemberService::class.java)
//
//    @Provides
//    @Singleton
//    fun provideBoardService(
//        @NetworkModule.MogInterceptorOkHttpClient retrofit: Retrofit
//    ): BoardService = retrofit.create(BoardService::class.java)
//
//    @Provides
//    @Singleton
//    fun provideChatService(
//        @NetworkModule.MogInterceptorOkHttpClient retrofit: Retrofit
//    ): ChatService = retrofit.create(ChatService::class.java)
//}