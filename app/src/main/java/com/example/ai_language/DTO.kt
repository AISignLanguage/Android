package com.example.ai_language

import com.google.gson.annotations.SerializedName

//RegisterActivity
data class UserDTO(
    @SerializedName("name") val name: String,
    @SerializedName("birthdate") val birthdate: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("nickname") val nickName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("profile_image_url") val profileImageUrl: String,
)

//Login
data class LoginRequestDTO(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)


data class LoginResponseDTO(
    @SerializedName("success") val success: Boolean
)

//Change Password
data class ChangePasswordRequestDTO(
    @SerializedName("password") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String,
    @SerializedName("checkPassword") val checkPassword: String

)

data class ChangePasswordResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("newPassword") val neqPassword: String
)


//confirm
data class ConfirmDTO(
    @SerializedName("word") val word: String
)
data class ConfirmedDTO(
    @SerializedName("ok") val ok: Boolean
)

//LoginActivity
data class LoginCheckDTO(
    @SerializedName("name") val name: String,
    @SerializedName("birthdate") val birthdate: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("nickname") val nickName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("profile_image_url") val profileImageUrl: String,
    @SerializedName("registerd_at") val registerdAt: String,
)

//CallListPage
data class PhoneNumberDTO(
    @SerializedName("phoneNumber") val phoneNumbers: List<String>
)

data class PhoneDTO(
    @SerializedName("name") val name: String?,
    @SerializedName("phoneNumber") val phoneNumbers: String?,
    @SerializedName("profileImageUrl") val profileImageUrl: String?
)

data class PhoneListDTO(
    val phones: List<List<PhoneDTO>>
)

//NewsPage
data class NewsDTO(
    @SerializedName("title") val title: String,      //제목
    @SerializedName("uri") val image: String,       //뉴스 썸네일 uri
    @SerializedName("content") val content: String  //내용
)

//Personalinfo
data class UpdateDTO(
    @SerializedName("uri") val uri: String,            //이미지 uri
    @SerializedName("nickName") val nickName: String,   //닉네임
    @SerializedName("loginInfo") val loginInfo: String, //로그인 정보 (앱/카톡)
    @SerializedName("password") val password: String    //비밀번호
)
//Unregister
data class DeleteDTO(
    @SerializedName("id") val id: String,
)
