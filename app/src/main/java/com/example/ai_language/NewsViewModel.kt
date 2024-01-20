package com.example.ai_language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class NewsViewModelItem(
    val title: String,
    val content: String,
    val imageResourceId: Int
)

class NewsViewModel : ViewModel() {
    private val _newsList = MutableLiveData<List<NewsViewModelItem>>()

    val newsList: LiveData<List<NewsViewModelItem>>get() = _newsList

    init {
        _newsList.value = listOf(
            NewsViewModelItem("뉴스 제목 1", "컨텐츠 1", R.drawable.newitem),
            NewsViewModelItem("뉴스 제목 2", "컨텐츠 2", R.drawable.newitem),
            NewsViewModelItem("뉴스 제목 3", "컨텐츠 3", R.drawable.newitem),
            NewsViewModelItem("뉴스 제목 4", "컨텐츠 4", R.drawable.newitem)
        )
    }
}
