package com.example.ai_language.domain.repository

import com.example.ai_language.domain.model.request.JoinDTO
import com.example.ai_language.domain.model.request.JoinOKDTO
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    // 회원가입
    suspend fun register(data: JoinDTO): Flow<JoinOKDTO>
}