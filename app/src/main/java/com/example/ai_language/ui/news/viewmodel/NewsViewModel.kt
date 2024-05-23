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
            NewsViewModelItem("ㄱ", "컨텐츠 1", R.drawable.giyeok),
            NewsViewModelItem("ㄴ", "컨텐츠 2", R.drawable.nieun),
            NewsViewModelItem("ㄷ", "컨텐츠 3", R.drawable.digeut),
            NewsViewModelItem("ㄹ", "컨텐츠 1", R.drawable.rieul),
            NewsViewModelItem("ㅁ", "컨텐츠 2", R.drawable.mieum),
            NewsViewModelItem("ㅂ", "컨텐츠 3", R.drawable.bieup),
            NewsViewModelItem("ㅅ", "컨텐츠 1", R.drawable.siot),
            NewsViewModelItem("ㅇ", "컨텐츠 2", R.drawable.leung),
            NewsViewModelItem("ㅈ", "컨텐츠 3", R.drawable.jieut),
            NewsViewModelItem("ㅊ", "컨텐츠 1", R.drawable.chieut),
            NewsViewModelItem("ㅌ", "컨텐츠 2", R.drawable.kieuk),
            NewsViewModelItem("ㅍ", "컨텐츠 3", R.drawable.pieup),
            NewsViewModelItem("ㅎ", "컨텐츠 1", R.drawable.hieut),
        )
    }

    private fun createVowelList(): List<NewsViewModelItem> {
        return listOf(
            NewsViewModelItem("ㅏ", "컨텐츠 1", R.drawable.a),
            NewsViewModelItem("ㅐ", "컨텐츠 2", R.drawable.ae),
            NewsViewModelItem("ㅑ", "컨텐츠 3", R.drawable.ya),
            NewsViewModelItem("ㅒ", "컨텐츠 1", R.drawable.yae),
            NewsViewModelItem("ㅓ", "컨텐츠 2", R.drawable.eo),
            NewsViewModelItem("ㅔ", "컨텐츠 3", R.drawable.e),
            NewsViewModelItem("ㅕ", "컨텐츠 1", R.drawable.yeo),
            NewsViewModelItem("ㅖ", "컨텐츠 2", R.drawable.ye),
            NewsViewModelItem("ㅗ", "컨텐츠 3", R.drawable.o),
            NewsViewModelItem("ㅘ", "컨텐츠 3", R.drawable.wa),
            NewsViewModelItem("ㅙ", "컨텐츠 3", R.drawable.wae),
            NewsViewModelItem("ㅛ", "컨텐츠 3", R.drawable.oe),
            NewsViewModelItem("ㅜ", "컨텐츠 3", R.drawable.yo),
            NewsViewModelItem("ㅝ", "컨텐츠 3", R.drawable.u),
            NewsViewModelItem("ㅞ", "컨텐츠 3", R.drawable.weo),
            NewsViewModelItem("ㅟ", "컨텐츠 3", R.drawable.we),
            NewsViewModelItem("ㅠ", "컨텐츠 3", R.drawable.wi),
            NewsViewModelItem("ㅡ", "컨텐츠 3", R.drawable.yu),
            NewsViewModelItem("ㅢ", "컨텐츠 3", R.drawable.ui),
            NewsViewModelItem("ㅣ", "컨텐츠 3", R.drawable.i),
            )
    }

    private fun createNumberList(): List<NewsViewModelItem> {
        return listOf(
            NewsViewModelItem("0", "컨텐츠 1", R.drawable.zero),
            NewsViewModelItem("1", "컨텐츠 1", R.drawable.one),
            NewsViewModelItem("2", "컨텐츠 2", R.drawable.two),
            NewsViewModelItem("3", "컨텐츠 3", R.drawable.three),
            NewsViewModelItem("4", "컨텐츠 1", R.drawable.four),
            NewsViewModelItem("5", "컨텐츠 2", R.drawable.five),
            NewsViewModelItem("6", "컨텐츠 3", R.drawable.six),
            NewsViewModelItem("7", "컨텐츠 1", R.drawable.seven),
            NewsViewModelItem("8", "컨텐츠 2", R.drawable.eight),
            NewsViewModelItem("9", "컨텐츠 3", R.drawable.nine),
            NewsViewModelItem("10", "컨텐츠 3", R.drawable.ten)
        )
    }



    fun addNews(newsItem: NewsViewModelItem) {
        val currentList = _consonantList.value?.toMutableList() ?: mutableListOf()
        currentList.add(newsItem)
        _consonantList.postValue(currentList)
    }

}
