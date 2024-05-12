package com.example.ai_language.data.source.google

import android.util.Log
import com.example.ai_language.data.remote.GoogleService
import com.example.ai_language.domain.model.response.DirectionsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GoogleDataSource @Inject constructor(
    private val googleService: GoogleService
) {

    fun getDirectionByGoogleApi(origin: String, destination: String, mode: String, apiKey: String) : Flow<DirectionsResponse> = flow{
        val result = googleService.getDirectionByGoogleApi(origin, destination, mode, apiKey)
        emit(result)
    }.catch {
        Log.e("Get Direction By Google Map Api Failure", it.message.toString())
    }
}