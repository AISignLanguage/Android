package com.example.ai_language.domain.model.response

import com.google.gson.annotations.SerializedName

data class ResponseBodys(
    val response: Response
)

data class Response(
    val header: Header,
    val body: Body
)

data class Header(
    val resultCode: String,
    val resultMsg: String
)

data class Body(
    val items: Items
)

data class Items(
    val item: List<Item>
)

data class Item(
    val title: String,
    val alternativeTitle: String?,
    val creator: String,
    val regDate: String,
    @SerializedName("collectionDb")
    val collectionDB: String,
    val referenceIdentifier: String,
    val url: String,
    val subDescription: String
)