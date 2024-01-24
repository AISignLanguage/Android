package com.example.ai_language

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class CallListItem(val name: String, val callNumber: String)
data class InviteListItem(val name: String, val callNumber: String)
class CallListViewModel : ViewModel() {
    var _callDataList = MutableLiveData<List<CallListItem>>()
    val callDataList : LiveData<List<CallListItem>>
        get() = _callDataList

    init {
        _callDataList.value = listOf(
            CallListItem("박지원", "010-1234-5678"),
            CallListItem("임다솔", "010-5322-1345"),
            CallListItem("신정인", "010-1209-0084"),
        )
        Log.d("로그", "뷰 모델 초기화 됨")
    }

    fun addListItem(item: CallListItem) {
        //liveData가 비었으면 빈 mutableList 생성후 넣음, 아니면 그냥 넣음
        val currentList = callDataList.value?.toMutableList() ?: mutableListOf()
        currentList.add(item)
        _callDataList.value = currentList // LiveData를 새로운 목록으로 업데이트
    }

    fun deleteListItem(position: Int) {
        val currentList = callDataList.value?.toMutableList() ?: mutableListOf()
        if (position in 0 until currentList.size) {
            currentList.removeAt(position)
            _callDataList.value = currentList // LiveData를 업데이트하여 항목이 삭제된 목록으로 설정
        }
    }
}

class InviteViewModel : ViewModel() {
    var _inviteDataList = MutableLiveData<List<InviteListItem>>()
    val inviteDataList : LiveData<List<InviteListItem>>
        get() = _inviteDataList
    init {
        _inviteDataList.value = listOf(
            InviteListItem("방경원", "010-8219-5021"),
            InviteListItem("임멍멍", "010-1523-8445"),
            InviteListItem("신정인", "010-1209-0084"),
        )
    }
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