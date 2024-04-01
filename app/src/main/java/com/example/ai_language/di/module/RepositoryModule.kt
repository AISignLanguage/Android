package com.example.ai_language.di.module

import com.example.ai_language.data.source.dictionary.DictionaryDataSource
import com.example.ai_language.data.source.dictionary.DictionaryRepositoryImpl
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

}