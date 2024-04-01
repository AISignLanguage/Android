package com.example.ai_language.di.module

import com.example.ai_language.data.source.CallListDataSource
import com.example.ai_language.data.source.CallListRepositoryImpl
import com.example.ai_language.data.source.DictionaryDataSource
import com.example.ai_language.data.source.DictionaryRepositoryImpl
import com.example.ai_language.domain.repository.CallListRepository
import com.example.ai_language.domain.repository.DictionaryRepository
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
        CallListRepositoryImpl(callListDataSource)

}