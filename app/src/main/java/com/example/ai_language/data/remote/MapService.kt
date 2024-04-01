package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.response.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {
    @GET("https://openapi.gg.go.kr/Signlangintrprtcenter")
    fun getInfo(
        @Query("KEY") key: String = "sample key",
        @Query("Type") type: String = "xml",
        @Query("pIndex") pageIndex: Int = 1,
        @Query("pSize") pageSize: Int = 100,
        @Query("SIGUN_NM") sigunNm: String? = null,
        @Query("SIGUN_CD") sigunCd: String? = null
    ): ApiResponse
}