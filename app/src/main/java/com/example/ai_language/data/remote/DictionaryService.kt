package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.response.ResponseBodys
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DictionaryService {
    @GET("/openapi/service/rest/meta13/getCTE01701")
    suspend fun getDictionaryByOpenApi(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: String,
        @Query("pageNo") pageNo: String
    ): ResponseBodys
}