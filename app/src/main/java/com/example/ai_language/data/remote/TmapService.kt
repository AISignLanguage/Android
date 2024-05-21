package com.example.ai_language.data.remote

import com.example.ai_language.BuildConfig
import com.example.ai_language.domain.model.request.TmapDTO
import com.example.ai_language.domain.model.response.FeatureCollection
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TmapService {
    @POST
        ("tmap/routes/pedestrian?version=1&callback=function")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "appKey: ${BuildConfig.Tmap_App_key}"
    )
    suspend fun getRouteBytMapApi(
        @Body request: TmapDTO
    ): FeatureCollection


    @POST
        ("tmap/routes?version=1&callback=function")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "appKey: ${BuildConfig.Tmap_Drive_App_key}"
    )
    suspend fun getRouteBytMapDriveApi(
        @Body request: TmapDTO
    ): FeatureCollection





}