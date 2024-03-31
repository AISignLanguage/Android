package com.example.ai_language.domain.model.response

import com.google.gson.annotations.SerializedName

data class ResponseBodys(
    val response: Response? = null
)

data class Response(
    val header: Header? = null,
    val body: Body? = null
)

data class Header(
    val resultCode: String = "",
    val resultMsg: String = ""
)

data class Body(
    val items: Items? = null
)

data class Items(
    val item: List<Item> = emptyList()
)

data class Item(
    val title: String="",
    val alternativeTitle: String?="",
    val creator: String="",
    val regDate: String="",
    @SerializedName("collectionDb")
    val collectionDB: String="",
    val referenceIdentifier: String="",
    val url: String="",
    val subDescription: String=""
)