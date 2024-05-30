package com.example.ai_language.data.source.dictionary

import com.example.ai_language.domain.model.response.ResponseBodys
import com.example.ai_language.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class DictionaryRepositoryImpl @Inject constructor(
    private val dataSource: DictionaryDataSource
) : DictionaryRepository{
    override suspend fun getDictionaryByOpenApi(
        serviceKey: String,
        numOfRows: String,
        pageNo: String
    ): Flow<ResponseBodys>  = dataSource.getDictionaryByOpenApi(serviceKey,numOfRows,pageNo)
}