package com.example.ai_language.domain.repository

import com.example.ai_language.domain.model.request.PhoneListDTO
import com.example.ai_language.domain.model.request.PhoneNumberDTO
import kotlinx.coroutines.flow.Flow

interface CallListRepository {
    suspend fun sendPhoneNumbers(phoneNumbers: PhoneNumberDTO): Flow<PhoneListDTO>
}

//suspend fun getDictionaryByOpenApi(serviceKey: String, numOfRows: String, pageNo: String): Flow<ResponseBodys>