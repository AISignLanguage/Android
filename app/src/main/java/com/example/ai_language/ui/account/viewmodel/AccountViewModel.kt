package com.example.ai_language.ui.account.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ai_language.domain.repository.AccountRepository
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
