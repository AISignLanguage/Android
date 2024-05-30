package com.example.ai_language.data.remote

import com.example.ai_language.BuildConfig
import com.example.ai_language.domain.model.response.ApiResponse
import com.example.ai_language.domain.model.response.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {
    @GET("Signlangintrprtcenter")
    suspend fun getCorporationByOpenApi(
        @Query("KEY") key: String = BuildConfig.Google_Map_key,
        @Query("Type") type: String = "json",
        @Query("pIndex") pageIndex: Int = 1,
        @Query("pSize") pageSize: Int = 100,
        @Query("SIGUN_NM") sigunNm: String? = null,
        @Query("SIGUN_CD") sigunCd: String? = null
    ): ApiResponse
}