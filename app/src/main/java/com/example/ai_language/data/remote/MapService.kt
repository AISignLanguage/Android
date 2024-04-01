package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.response.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {
    @GET("Signlangintrprtcenter")
    suspend fun getCorporationByOpenApi(
        @Query("KEY") key: String = "4b7ecc6c16b1492db814d065c2e0e16f",
        @Query("Type") type: String = "json",
        @Query("pIndex") pageIndex: Int = 1,
        @Query("pSize") pageSize: Int = 100,
        @Query("SIGUN_NM") sigunNm: String? = null,
        @Query("SIGUN_CD") sigunCd: String? = null
    ): ApiResponse
}