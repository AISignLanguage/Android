package com.example.ai_language.data.source.account

import com.example.ai_language.domain.model.request.LoginCheckDTO
import com.example.ai_language.domain.model.request.UserDTO
import com.example.ai_language.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AccountRepositoryImpl @Inject constructor(
    private val dataSource: AccountDataSource
) : AccountRepository {

    // 회원가입
    override suspend fun sendData(
        data: UserDTO
    ): Flow<LoginCheckDTO> = dataSource.sendData(data)
}