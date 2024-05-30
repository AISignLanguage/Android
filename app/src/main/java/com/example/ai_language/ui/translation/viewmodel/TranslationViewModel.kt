package com.example.ai_language.ui.translation.viewmodel

import android.util.Log
import android.util.LogPrinter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_language.domain.model.response.ResultTypeText
import com.example.ai_language.domain.model.response.TaskId
import com.example.ai_language.domain.model.response.WavUrlResponse
import com.example.ai_language.domain.model.response.Wavresponse
import com.example.ai_language.domain.repository.FastApiRepository
import com.example.ai_language.domain.repository.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.security.PrivateKey
import javax.inject.Inject

@HiltViewModel
class TranslationViewModel @Inject constructor(
    private val translationRepository: TranslationRepository,
    private val fastApiRepository: FastApiRepository
) : ViewModel() {
    private var _remote = MutableStateFlow(TaskId())
    val remote: StateFlow<TaskId> = _remote

    private var _local = MutableStateFlow(TaskId())
    val local: StateFlow<TaskId> = _local

    private var _result = MutableStateFlow(ResultTypeText())
    val result: StateFlow<ResultTypeText> = _result

    private var _wavUrl = MutableStateFlow(Wavresponse())
    val wavUrl : StateFlow<Wavresponse> = _wavUrl

    //wav -> 태스크 ID
    fun postTextByRemoteFile(keyId: String, keySecret: String, lang: String, remotePath: String) {
        viewModelScope.launch {
            try {
                translationRepository.postTextByRemoteFile(keyId, keySecret, lang, remotePath)
                    .collect {
                        _remote.value = it.body()!!
                    }
            } catch (e: Exception) {
                Log.e("Remote Error", e.message.toString())
            }
        }
    }


    fun postTextByLocalFile(
        keyId: String,
        keySecret: String,
        lang: RequestBody,
        file: MultipartBody.Part
    ) {
        viewModelScope.launch {
            try {
                translationRepository.postTextByLocalFile(keyId, keySecret, lang, file).collect {
                    _local.value = it.body()!!
                }
            } catch (e: Exception) {
                Log.e("Local Error", e.message.toString())
            }
        }
    }

    //
    fun getTextFileBySpeechFlowApi(
        keyId: String,
        keySecret: String,
        taskId: String,
        resultType: Int
    ) {
        viewModelScope.launch {
            try {
                translationRepository.getTextFileBySpeechFlowApi(
                    keyId,
                    keySecret,
                    taskId,
                    resultType
                ).collect {
                    _result.value = it.body()!!
                }
            } catch (e: Exception) {
                Log.e("Result Error", e.message.toString())
            }
        }
    }

    fun getWavUrl(youtube_url : WavUrlResponse){
        viewModelScope.launch {
            try{
                fastApiRepository.getWavUrl(youtube_url).collect{
                    _wavUrl.value = it
                }
            }catch (e :Exception){
                Log.e("Fast Api Error", e.message.toString())
            }
        }
    }


}