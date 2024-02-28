package com.example.ai_language

import androidx.lifecycle.ViewModel
import kotlin.math.log

data class LoginItem(val email: String?, val password: String?)
class EncryptedSharedPreferencesViewModel : ViewModel() {
    private val loginDataList : MutableMap<String, String> = HashMap()

    fun setLoginItem(email: String?, password: String?) {
        email?.let { loginDataList.put("email", email) }
        password?.let { loginDataList.put("password", password) }
    }

    fun getLoginItem() : Map<String, String> {
        val email = loginDataList.get("email")
        val password = loginDataList.get("password")

        email?.let { loginDataList["email"] = it }
        password?.let { loginDataList["password"] = password }

        return loginDataList
    }

}