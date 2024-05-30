package com.example.ai_language.data.source.call

import android.util.Log
import com.example.ai_language.data.remote.CallListService
import com.example.ai_language.domain.model.request.PhoneListDTO
import com.example.ai_language.domain.model.request.PhoneNumberDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CallListDataSource @Inject constructor(
  private val callListService: CallListService
) {
    fun sendPhoneNumbers(phoneNumbers: PhoneNumberDTO): Flow<PhoneListDTO> = flow {
        val result = callListService.sendPhoneNumbers(phoneNumbers)
        emit(result)
    }.catch {
        Log.e("Post Call By sendPhoneNumbers Failure", it.message.toString())
    }
}