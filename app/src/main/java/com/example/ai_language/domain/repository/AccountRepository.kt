package com.example.ai_language.domain.repository

import com.example.ai_language.domain.model.request.LoginCheckDTO
import com.example.ai_language.domain.model.request.UserDTO
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    // 회원가입
    suspend fun sendData(data: UserDTO): Flow<LoginCheckDTO>
}