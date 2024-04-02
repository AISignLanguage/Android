package com.example.ai_language.data.source.call

import com.example.ai_language.domain.model.request.PhoneListDTO
import com.example.ai_language.domain.model.request.PhoneNumberDTO
import com.example.ai_language.domain.repository.CallListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CallRepositoryImpl @Inject constructor(
    private val dataSource: CallListDataSource
) : CallListRepository {
    override suspend fun sendPhoneNumbers(
        phoneNumbers: PhoneNumberDTO
    ): Flow<PhoneListDTO> = dataSource.sendPhoneNumbers(phoneNumbers)

}

//class DictionaryRepositoryImpl @Inject constructor(
//    private val dataSource: DictionaryDataSource
//) : DictionaryRepository {
//
//    override suspend fun getDictionaryByOpenApi(
//        serviceKey: String,
//        numOfRows: String,
//        pageNo: String
//    ): Flow<ResponseBodys> = dataSource.getDictionaryByOpenApi(serviceKey,numOfRows,pageNo)
//
//
//}