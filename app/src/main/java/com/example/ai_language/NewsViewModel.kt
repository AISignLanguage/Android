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
    //자음
    private val _consonantList = MutableLiveData<List<NewsViewModelItem>>()
    val consonantList: LiveData<List<NewsViewModelItem>> get() = _consonantList

    //모음
    private val _vowelList = MutableLiveData<List<NewsViewModelItem>>()
    val vowelList: LiveData<List<NewsViewModelItem>> get() = _vowelList

    //숫자
    private val _numberList = MutableLiveData<List<NewsViewModelItem>>()
    val numberList: LiveData<List<NewsViewModelItem>> get() = _numberList


    init {
        initializeData()

    }

    fun initializeData(){
        _consonantList.postValue(
            listOf(
                NewsViewModelItem("자음", "컨텐츠 1", R.drawable.newitem),
                NewsViewModelItem("자음", "컨텐츠 2", R.drawable.newitem),
                NewsViewModelItem("자음", "컨텐츠 3", R.drawable.newitem),
                NewsViewModelItem("자음", "컨텐츠 1", R.drawable.newitem),
                NewsViewModelItem("자음", "컨텐츠 2", R.drawable.newitem),
                NewsViewModelItem("자음", "컨텐츠 3", R.drawable.newitem),
                NewsViewModelItem("자음", "컨텐츠 1", R.drawable.newitem),
                NewsViewModelItem("자음", "컨텐츠 2", R.drawable.newitem),
                NewsViewModelItem("자음", "컨텐츠 3", R.drawable.newitem),
                NewsViewModelItem("자음", "컨텐츠 1", R.drawable.newitem),
                NewsViewModelItem("자음", "컨텐츠 2", R.drawable.newitem),
                NewsViewModelItem("자음", "컨텐츠 3", R.drawable.newitem),
                NewsViewModelItem("자음", "컨텐츠 1", R.drawable.newitem),
                NewsViewModelItem("자음", "컨텐츠 2", R.drawable.newitem)
            )
        )

        _vowelList.postValue(
            listOf(
                NewsViewModelItem("모음", "컨텐츠 1", R.drawable.newitem),
                NewsViewModelItem("모음", "컨텐츠 2", R.drawable.newitem),
                NewsViewModelItem("모음", "컨텐츠 3", R.drawable.newitem),
                NewsViewModelItem("모음", "컨텐츠 1", R.drawable.newitem),
                NewsViewModelItem("모음", "컨텐츠 2", R.drawable.newitem),
                NewsViewModelItem("모음", "컨텐츠 3", R.drawable.newitem),
                NewsViewModelItem("모음", "컨텐츠 1", R.drawable.newitem),
                NewsViewModelItem("모음", "컨텐츠 2", R.drawable.newitem),
                NewsViewModelItem("모음", "컨텐츠 3", R.drawable.newitem),
                NewsViewModelItem("모음", "컨텐츠 3", R.drawable.newitem)
            )
        )

        _numberList.postValue(
            listOf(
                NewsViewModelItem("숫자", "컨텐츠 1", R.drawable.newitem),
                NewsViewModelItem("숫자", "컨텐츠 2", R.drawable.newitem),
                NewsViewModelItem("숫자", "컨텐츠 3", R.drawable.newitem),
                NewsViewModelItem("숫자", "컨텐츠 1", R.drawable.newitem),
                NewsViewModelItem("숫자", "컨텐츠 2", R.drawable.newitem),
                NewsViewModelItem("숫자", "컨텐츠 3", R.drawable.newitem),
                NewsViewModelItem("숫자", "컨텐츠 1", R.drawable.newitem),
                NewsViewModelItem("숫자", "컨텐츠 2", R.drawable.newitem),
                NewsViewModelItem("숫자", "컨텐츠 3", R.drawable.newitem),
                NewsViewModelItem("숫자", "컨텐츠 3", R.drawable.newitem)
            )
        )
    }



    fun addNews(newsItem: NewsViewModelItem) {
        val currentList = _consonantList.value?.toMutableList() ?: mutableListOf()
        currentList.add(newsItem)
        _consonantList.postValue(currentList)
    }

}

