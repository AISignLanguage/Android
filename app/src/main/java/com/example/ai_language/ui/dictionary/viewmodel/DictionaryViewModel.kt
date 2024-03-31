package com.example.ai_language.ui.dictionary.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_language.domain.model.response.ResponseBodys
import com.example.ai_language.domain.repository.DictionaryRepository
import com.example.ai_language.ui.dictionary.data.Tagdata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val dictionaryRepository: DictionaryRepository
) : ViewModel() {

    private var _dicList = MutableStateFlow(ResponseBodys())
    val dicList: StateFlow<ResponseBodys> = _dicList

    private var _tagList = MutableStateFlow(Tagdata())
    val tagList = _tagList


    private val tag_list = MutableLiveData<ArrayList<Tagdata>>()
    val tag_data: LiveData<ArrayList<Tagdata>> get() = tag_list
    private var tag_items = ArrayList<Tagdata>()

    fun getDictionaryByOpenApi(serviceKey: String, numOfRows: String, pageNo: String) {
        viewModelScope.launch {
            try {
                dictionaryRepository.getDictionaryByOpenApi(serviceKey, numOfRows, pageNo).collect {
                    _dicList.value = it
                }
            } catch (e: Exception) {
                Log.e("Dictionary Error", e.message.toString())
            }
        }
    }

    //태그 리스트
    init {
        tag_list.value = tag_items
    }

    fun tagAddData(item: Tagdata) {
        tag_items.add(item)
        tag_list.value = tag_items
    }
}