package com.example.ai_language

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
data class DicPic(var dicpic: Uri, var ex: String)

data class Tagdata(val tag: String, var isPressed: Boolean = false)
class DictionaryViewModel : ViewModel (){
    private val dic_list = MutableLiveData<ArrayList<DicPic>>()
    val dic_data : LiveData<ArrayList<DicPic>>get() = dic_list
    private var dic_items = ArrayList<DicPic>()
    //딕셔너리 리스트

    private val tag_list = MutableLiveData<ArrayList<Tagdata>>()
    val tag_data : LiveData<ArrayList<Tagdata>>get() = tag_list
    private var tag_items = ArrayList<Tagdata>()
    //태그 리스트
    init {
        dic_list.value = dic_items
        tag_list.value = tag_items
    }
    fun dicAddData(item : DicPic){
        dic_items.add(item)
        dic_list.postValue(dic_items)
    }
    fun dicUpdateData(item : DicPic, pos : Int){
        dic_items[pos] = item
        dic_list.value = dic_items
    }
    fun dicRemoveData(pos : Int){
        dic_items.removeAt(pos)
        dic_list.value = dic_items
    }
    fun tagAddData(item : Tagdata){
        tag_items.add(item)
        tag_list.value = tag_items
    }
    fun tagUpdateData(item : Tagdata, pos : Int){
        tag_items[pos] = item
        tag_list.value = tag_items
    }
    fun tagRemoveData(pos : Int){
        tag_items.removeAt(pos)
        tag_list.value = tag_items
    }
}