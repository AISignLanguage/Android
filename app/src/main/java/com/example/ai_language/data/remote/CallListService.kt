package com.example.ai_language.data.remote

import com.example.ai_language.domain.model.request.PhoneListDTO
import com.example.ai_language.domain.model.request.PhoneNumberDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface CallListService {
    @POST("api/mog/user/send-callList") // 전화번호부
    suspend fun sendPhoneNumbers(
        @Body phoneNumberDTO: PhoneNumberDTO
    ): PhoneListDTO
}
//interface DictionaryService {
//    @GET("/openapi/service/rest/meta13/getCTE01701")
//    @Headers("Content-Type: application/json", "Accept: application/json")
//    suspend fun getDictionaryByOpenApi(
//        @Query("serviceKey") serviceKey: String,
//        @Query("numOfRows") numOfRows: String,
//        @Query("pageNo") pageNo: String
//    ): ResponseBodys
//}