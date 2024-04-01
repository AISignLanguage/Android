package com.example.ai_language.domain.model.request

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

//IdFindFragment (아이디 찾기 - 이메일 인증)
data class FindIdDTO(
    @SerializedName("name") val name: String,
    @SerializedName("phone_number") val phoneNumber: String
)

data class GetIdDTO(
    @SerializedName("email") val email: String
)

//PasswordFindFragment (비밀번호 찾기)
data class FindPwdDTO(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String
)

data class FindPwdOk(
    @SerializedName("success_find_pwd") val success_find_pwd: Boolean
)

data class LoginResponseDTO(
    @SerializedName("success") val success: Boolean
)

//Check Password
data class CheckPasswordRequestDTO(
    @SerializedName("email") val email: String
)


data class CheckPasswordResponseDTO(
    @SerializedName("password") val password: String
)


//Change Password
data class ChangePasswordRequestDTO(
    @SerializedName("email") val email: String,
    @SerializedName("newPassword") val currentPassword: String
)

data class ChangePasswordResponseDTO(
    @SerializedName("success") val success: Boolean
)

//Delete User
data class DeleteUserRequestDTO(
    @SerializedName("email") val email: String
)

data class DeleteUserResponseDTO(
    @SerializedName("success") val success: Boolean
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

//CallListPage (전화번호 DB에서 불러오기)
data class PhoneNumberDTO(
    @SerializedName("phoneNumber") val phoneNumbers: List<String>? = null
)

data class PhoneDTO(
    @SerializedName("name") val name: String? = null,
    @SerializedName("phoneNumber") val phoneNumbers: String? = null,
    @SerializedName("profileImageUrl") val profileImageUrl: String? = null
)

data class PhoneListDTO(
    val phones: List<List<PhoneDTO>>? = null
)

//NewsPage
data class NewsDTO(
    @SerializedName("title") val title: String,      //제목
    @SerializedName("uri") val image: String,       //뉴스 썸네일 uri
    @SerializedName("content") val content: String  //내용
)

//Personalinfo (개인 정보 불러오기)
data class ProfileRequestDTO(
    @SerializedName("email") val email: String
)

data class GetProfileDTO(
    @SerializedName("url") val url: String,
    @SerializedName("name") val name: String,
    @SerializedName("nickName") val nickName: String,
    //@SerializedName("loginInfo") val loginInfo: String, //로그인 정보 (앱/카톡)
    @SerializedName("password") val password: String,    //비밀번호
    @SerializedName("birthdate") val birthdate: String,
    @SerializedName("phone_number") val phoneNumber: String
)

// ChangeNicknameActivity (닉네임 변경)
data class ChangeNickNameDTO(
    @SerializedName("originalNickname") val originalNickname: String,
    @SerializedName("nickname") val nickname: String
)

data class ChangeNickNameResultDTO(
    @SerializedName("result") val result: Int
)

//Unregister
data class DeleteDTO(
    @SerializedName("id") val id: String,
)
