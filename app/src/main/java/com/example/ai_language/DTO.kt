package com.example.ai_language

import com.google.gson.annotations.SerializedName

//RegisterActivity
//data class UserDTO(
//    @SerializedName("name") val name: String,
//    @SerializedName("birthdate") val birthdate: String,
//    @SerializedName("email") val email: String,
//    @SerializedName("password") val password: String,
//    @SerializedName("nickName") val nickName: String,
//    @SerializedName("phoneNumber") val phoneNumber: String
//)
//
////LoginActivity
//data class LoginCheckDTO(
//    @SerializedName("id_ok") val loginCheck: Boolean, //아이디 일치 여부
//    @SerializedName("pwd_ok") val pwdCheck: Boolean   //비밀번호 일치 여부
//)
//
////CallListPage
//data class CallListDTO(
//    @SerializedName("uri") val pwdCheck: Boolean, //이미지 uri
//    @SerializedName("installCheck") val installCheck: Boolean //앱 다운 여부
//)
//
////Personalinfo
//data class UpdateDTO(
//    @SerializedName("uri") val uri: Boolean,            //이미지 uri
//    @SerializedName("nickName") val nickName: String,   //닉네임
//    @SerializedName("loginInfo") val loginInfo: String, //로그인 정보 (앱/카톡)
//    @SerializedName("password") val password: String    //비밀번호
//)
////Unregister
//data class DeleteDTO(
//    @SerializedName("id") val id: String,
//)

data class UserDTO(
    @SerializedName("username")
    val username: String,

    @SerializedName("usernick")
    val usernick: String
)

data class ResponseDTO(
    @SerializedName("logIn_ok")
    val response: Boolean
)
