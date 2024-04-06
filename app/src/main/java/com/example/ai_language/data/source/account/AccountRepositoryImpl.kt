package com.example.ai_language.data.source.account

import com.example.ai_language.data.source.call.CallListDataSource
import com.example.ai_language.domain.model.request.LoginCheckDTO
import com.example.ai_language.domain.model.request.PhoneListDTO
import com.example.ai_language.domain.model.request.PhoneNumberDTO
import com.example.ai_language.domain.model.request.UserDTO
import com.example.ai_language.domain.model.response.ResponseBodys
import com.example.ai_language.domain.repository.AccountRepository
import com.example.ai_language.domain.repository.CallListRepository
import com.example.ai_language.domain.repository.DictionaryRepository
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