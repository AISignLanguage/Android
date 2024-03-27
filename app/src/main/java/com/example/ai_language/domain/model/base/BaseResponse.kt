package com.example.ai_language.domain.model.base

data class BaseResponse<T>(
    val status: String = "",
    val message: String = "",
    val result: T? = null
)