package com.example.ai_language.data.source.account

import android.util.Log
import com.example.ai_language.data.remote.AccountService
import com.example.ai_language.domain.model.request.LoginCheckDTO
import com.example.ai_language.domain.model.request.UserDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AccountDataSource @Inject constructor(
    private val accountService: AccountService
) {
    // 회원가입
    fun sendData(data: UserDTO): Flow<LoginCheckDTO> = flow {
        val result = accountService.sendData(data)
        emit(result)
    } .catch {
        Log.e("Post Register By sendDta Failure", it.message.toString())
    }
}
