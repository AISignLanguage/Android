package com.example.ai_language

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class NewsViewModelItem(
    val title: String,
    val content: String,
    val imageResourceId: Int
)

class NewsViewModel : ViewModel() {
    private val _newsList = MutableLiveData<List<NewsViewModelItem>>()
    val newsList: LiveData<List<NewsViewModelItem>> get() = _newsList

    init {
        _newsList.postValue(
            listOf(
                NewsViewModelItem("뉴스", "컨텐츠 1", R.drawable.newitem),
                NewsViewModelItem("뉴스", "컨텐츠 2", R.drawable.newitem),
                NewsViewModelItem("뉴스", "컨텐츠 3", R.drawable.newitem)
            )
        )
    }


    fun addNews(newsItem: NewsViewModelItem) {
        val currentList = _newsList.value?.toMutableList() ?: mutableListOf()
        currentList.add(newsItem)
        _newsList.postValue(currentList)
    }

}

