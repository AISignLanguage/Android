package com.example.ai_language.ui.call.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_language.domain.model.request.PhoneListDTO
import com.example.ai_language.domain.model.request.PhoneNumberDTO
import com.example.ai_language.domain.repository.CallListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CallListItem(val name: String?, val callNumber: String?, val imageUrl: String?)
data class InviteListItem(val name: String, val callNumber: String)

@HiltViewModel
class CallListViewModel @Inject constructor(
    private val callListRepository: CallListRepository
) :ViewModel() {

//    var _callDataList = MutableLiveData<List<CallListItem>>()
//    val callDataList: LiveData<List<CallListItem>>
//        get() = _callDataList

    // CallListViewModel에 플로우 적용
    private var _callDataList = MutableStateFlow(PhoneListDTO(emptyList()))
    val callDataList: StateFlow<PhoneListDTO> = _callDataList

    fun sendPhoneNumbers(phoneNumbers: PhoneNumberDTO){
        viewModelScope.launch {
            try {
                callListRepository.sendPhoneNumbers(phoneNumbers).collect{
                    _callDataList.value = it
                }
            }catch (e : Exception){
                Log.e("CallListViewModel Error", e.message.toString())
            }
        }
    }

//    fun addListItem(item: CallListItem) {
//        //liveData가 비었으면 빈 mutableList 생성후 넣음, 아니면 그냥 넣음
//        val currentList = callDataList.value?.toMutableList() ?: mutableListOf()
//        currentList.add(item)
//        _callDataList.value = currentList // LiveData를 새로운 목록으로 업데이트
//        Log.d("로그", "addListItem " + "${_callDataList.value}")
//    }

}

class InviteViewModel : ViewModel() {
    var _inviteDataList = MutableLiveData<List<InviteListItem>>()
    val inviteDataList: LiveData<List<InviteListItem>>
        get() = _inviteDataList

    fun addListItem(item: InviteListItem) {
        //liveData가 비었으면 빈 mutableList 생성후 넣음, 아니면 그냥 넣음
        val currentList = inviteDataList.value?.toMutableList() ?: mutableListOf()
        currentList.add(item)
        _inviteDataList.value = currentList // LiveData를 새로운 목록으로 업데이트
    }

    fun deleteListItem(position: Int) {
        val currentList = inviteDataList.value?.toMutableList() ?: mutableListOf()
        if (position in 0 until currentList.size) {
            currentList.removeAt(position)
            _inviteDataList.value = currentList // LiveData를 업데이트하여 항목이 삭제된 목록으로 설정
        }
    }
}