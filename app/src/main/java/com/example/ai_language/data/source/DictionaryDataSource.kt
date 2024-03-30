package com.example.ai_language.data.source

import android.util.Log
import com.example.ai_language.data.remote.DictionaryService
import com.example.ai_language.domain.model.response.ResponseBodys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DictionaryDataSource @Inject constructor(
    private val dictionaryService : DictionaryService
) {
    fun getDictionaryByOpenApi(serviceKey: String, numOfRows: String, pageNo: String) : Flow<ResponseBodys> = flow{
        val result =dictionaryService.getDictionaryByOpenApi(serviceKey,numOfRows,pageNo)
        emit(result)
    }.catch {
        Log.e("Get Dictionary By OpenApi Failure", it.message.toString())
    }
}