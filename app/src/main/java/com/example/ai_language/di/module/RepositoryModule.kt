package com.example.ai_language.di.module

import com.example.ai_language.data.source.call.CallListDataSource
import com.example.ai_language.data.source.call.CallRepositoryImpl
import com.example.ai_language.data.source.Map.MapDataSource
import com.example.ai_language.data.source.Map.MapRepositoryImpl
import com.example.ai_language.data.source.account.AccountDataSource
import com.example.ai_language.data.source.account.AccountRepositoryImpl
import com.example.ai_language.data.source.dictionary.DictionaryDataSource
import com.example.ai_language.data.source.dictionary.DictionaryRepositoryImpl
import com.example.ai_language.domain.repository.AccountRepository
import com.example.ai_language.data.source.naver.NaverDataSource
import com.example.ai_language.data.source.naver.NaverRepositoryImpl
import com.example.ai_language.data.source.tmap.TMapDataSource
import com.example.ai_language.data.source.tmap.TMapRepositoryImpl
import com.example.ai_language.data.source.translation.TranslationDataSource
import com.example.ai_language.data.source.translation.TranslationRepositoryImpl
import com.example.ai_language.data.source.youtube.YoutubeDataSource
import com.example.ai_language.data.source.youtube.YoutubeRepositoryImpl
import com.example.ai_language.domain.repository.CallListRepository
import com.example.ai_language.domain.repository.DictionaryRepository
import com.example.ai_language.domain.repository.MapRepository
import com.example.ai_language.domain.repository.NaverRepository
import com.example.ai_language.domain.repository.TMapRepository
import com.example.ai_language.domain.repository.TranslationRepository
import com.example.ai_language.domain.repository.YoutubeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideDictionaryRepository(dictionaryDataSource: DictionaryDataSource): DictionaryRepository =
        DictionaryRepositoryImpl(dictionaryDataSource)

    @Singleton
    @Provides
    fun provideCallListRepository(callListDataSource: CallListDataSource): CallListRepository =
        CallRepositoryImpl(callListDataSource)

    @Singleton
    @Provides
    fun provideCorporationRepository(mapDataSource: MapDataSource) : MapRepository =
        MapRepositoryImpl(mapDataSource)

    @Singleton
    @Provides
    fun provideAccountRegisterRepository(accountDataSource: AccountDataSource) : AccountRepository =
        AccountRepositoryImpl(accountDataSource)
    @Singleton
    @Provides
    fun provideNaverRepository(naverDataSource: NaverDataSource) : NaverRepository =
        NaverRepositoryImpl(naverDataSource)

    @Singleton
    @Provides
    fun provideTMapRepository(tMapDataSource: TMapDataSource) : TMapRepository =
        TMapRepositoryImpl(tMapDataSource)

    @Singleton
    @Provides
    fun provideTranslationRepository(translationDataSource: TranslationDataSource) : TranslationRepository =
        TranslationRepositoryImpl(translationDataSource)

    @Singleton
    @Provides
    fun provideYoutubePlayerRepository(youtubeDataSource: YoutubeDataSource) : YoutubeRepository =
        YoutubeRepositoryImpl(youtubeDataSource)



}