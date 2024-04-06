package com.example.ai_language.ui.account.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_language.domain.model.request.PhoneListDTO
import com.example.ai_language.domain.model.request.PhoneNumberDTO
import com.example.ai_language.domain.repository.AccountRepository
import com.example.ai_language.domain.repository.CallListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel(){

    // AccountViewModel에 플로우 적용
    //private var _registerDataList = MutableList()
}

//class CallListViewModel @Inject constructor(
//    private val callListRepository: CallListRepository
//) :ViewModel() {
//
//    // CallListViewModel에 플로우 적용
//    private var _callDataList = MutableStateFlow(PhoneListDTO(emptyList()))
//    val callDataList: StateFlow<PhoneListDTO> = _callDataList
//
//    fun sendPhoneNumbers(phoneNumbers: PhoneNumberDTO){
//        viewModelScope.launch {
//            try {
//                callListRepository.sendPhoneNumbers(phoneNumbers).collect {
//                    Log.d("로그", "sendPhoneNumbers 호출 ${_callDataList.value}")
//                    _callDataList.value = it
//                }
//            } catch (e: Exception) {
//                Log.e("CallListViewModel Error", e.message.toString())
//            }
//        }
//    }
