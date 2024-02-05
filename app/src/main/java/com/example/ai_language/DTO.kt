package com.example.ai_language

import com.google.gson.annotations.SerializedName

data class RegisterDTO(
    @SerializedName("name") val name: String,
    @SerializedName("birthdate") val birthdate: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("nickName") val nickName: String,
    @SerializedName("phoneNumber") val phoneNumber: String
)
data class LoginCheckDTO(
    @SerializedName("logIn_ok") val response: Boolean,

)