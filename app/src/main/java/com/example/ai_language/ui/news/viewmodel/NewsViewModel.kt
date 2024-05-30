package com.example.ai_language.ui.news.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ai_language.R

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

    private fun initializeData(){
        _consonantList.postValue(createConsonantList())
        _vowelList.postValue(createVowelList())
        _numberList.postValue(createNumberList())
    }

    private fun createConsonantList(): List<NewsViewModelItem> {
        return listOf(
            NewsViewModelItem("ㄱ", "", R.drawable.giyeok),
            NewsViewModelItem("ㄴ", "", R.drawable.nieun),
            NewsViewModelItem("ㄷ", "", R.drawable.digeut),
            NewsViewModelItem("ㄹ", "", R.drawable.rieul),
            NewsViewModelItem("ㅁ", "", R.drawable.mieum),
            NewsViewModelItem("ㅂ", "", R.drawable.bieup),
            NewsViewModelItem("ㅅ", "", R.drawable.siot),
            NewsViewModelItem("ㅇ", "", R.drawable.leung),
            NewsViewModelItem("ㅈ", "", R.drawable.jieut),
            NewsViewModelItem("ㅊ", "", R.drawable.chieut),
            NewsViewModelItem("ㅌ", "", R.drawable.kieuk),
            NewsViewModelItem("ㅍ", "", R.drawable.pieup),
            NewsViewModelItem("ㅎ", "", R.drawable.hieut),
        )
    }

    private fun createVowelList(): List<NewsViewModelItem> {
        return listOf(
            NewsViewModelItem("ㅏ", "", R.drawable.a),
            NewsViewModelItem("ㅐ", "", R.drawable.ae),
            NewsViewModelItem("ㅑ", "", R.drawable.ya),
            NewsViewModelItem("ㅒ", "", R.drawable.yae),
            NewsViewModelItem("ㅓ", "", R.drawable.eo),
            NewsViewModelItem("ㅔ", "", R.drawable.e),
            NewsViewModelItem("ㅕ", "", R.drawable.yeo),
            NewsViewModelItem("ㅖ", "", R.drawable.ye),
            NewsViewModelItem("ㅗ", "", R.drawable.o),
            NewsViewModelItem("ㅘ", "", R.drawable.wa),
            NewsViewModelItem("ㅙ", "", R.drawable.wae),
            NewsViewModelItem("ㅛ", "", R.drawable.oe),
            NewsViewModelItem("ㅜ", "", R.drawable.yo),
            NewsViewModelItem("ㅝ", "", R.drawable.u),
            NewsViewModelItem("ㅞ", "", R.drawable.weo),
            NewsViewModelItem("ㅟ", "", R.drawable.we),
            NewsViewModelItem("ㅠ", "", R.drawable.wi),
            NewsViewModelItem("ㅡ", "", R.drawable.yu),
            NewsViewModelItem("ㅢ", "", R.drawable.ui),
            NewsViewModelItem("ㅣ", "", R.drawable.i),
            )
    }

    private fun createNumberList(): List<NewsViewModelItem> {
        return listOf(
            NewsViewModelItem("0", "", R.drawable.zero),
            NewsViewModelItem("1", "", R.drawable.one),
            NewsViewModelItem("2", "", R.drawable.two),
            NewsViewModelItem("3", "", R.drawable.three),
            NewsViewModelItem("4", "", R.drawable.four),
            NewsViewModelItem("5", "", R.drawable.five),
            NewsViewModelItem("6", "", R.drawable.six),
            NewsViewModelItem("7", "", R.drawable.seven),
            NewsViewModelItem("8", "", R.drawable.eight),
            NewsViewModelItem("9", "", R.drawable.nine),
            NewsViewModelItem("10", "", R.drawable.ten)
        )
    }



    fun addNews(newsItem: NewsViewModelItem) {
        val currentList = _consonantList.value?.toMutableList() ?: mutableListOf()
        currentList.add(newsItem)
        _consonantList.postValue(currentList)
    }

}
