package com.example.ai_language.data.source.account

import com.example.ai_language.domain.model.request.JoinDTO
import com.example.ai_language.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AccountRepositoryImpl @Inject constructor(
    private val dataSource: AccountDataSource
) : AccountRepository {

    // 회원가입
    override suspend fun register(
        data: JoinDTO
    ): Flow<String> = dataSource.register(data)
}