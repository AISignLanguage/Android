package com.example.ai_language.data.source.account

import android.util.Log
import com.example.ai_language.data.remote.AccountService
import com.example.ai_language.domain.model.request.JoinDTO
import com.example.ai_language.domain.model.request.JoinOKDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AccountDataSource @Inject constructor(
    private val accountService: AccountService
) {
    // 회원가입
    fun register(data: JoinDTO): Flow<JoinOKDTO> = flow {
        val result = accountService.register(data)
        Log.e("Post Register By register success", "AccountDataSource result: $result")
        emit(result)
    } .catch {
        val result = accountService.register(data)
        Log.e("Post Register By register Failure", "${it.message.toString()}, ${"result: $result"}")
    }
}
