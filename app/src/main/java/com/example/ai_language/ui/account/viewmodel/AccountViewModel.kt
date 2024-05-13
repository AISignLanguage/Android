package com.example.ai_language.ui.account.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_language.domain.model.request.JoinDTO
import com.example.ai_language.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _loginCheck = MutableStateFlow<String?>(null)
    val loginCheck: StateFlow<String?> = _loginCheck

    fun register(joinDTO: JoinDTO) {
        viewModelScope.launch {
            try {
                accountRepository.register(joinDTO).collect {
                    _loginCheck.value = it
                    Log.e("로그", "register 뷰모델: $it")
                }
            } catch (e: Exception) {
                Log.e("로그", e.message.toString())
            }
        }
    }
}
