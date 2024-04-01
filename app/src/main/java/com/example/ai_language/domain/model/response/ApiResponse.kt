package com.example.ai_language.domain.model.response

data class ApiResponse(
    val Signlangintrprtcenter: List<SignLangIntrprtcenter> = emptyList()
)

data class SignLangIntrprtcenter(
    val head: List<Head> = emptyList(),
    val row: List<BusinessInfo> = emptyList()
)

data class Head(
    val list_total_count: Int = 0,
    val RESULT: Result = Result(),
    val api_version: String = ""
)

data class Result(
    val CODE: String = "",
    val MESSAGE: String = ""
)

data class BusinessInfo(
    val SIGUN_NM: String = "",
    val SIGUN_CD: String = "",
    val BIZPLC_NM: String = "",
    val LICENSG_DE: String = "",
    val BSN_STATE_NM: String = "",
    val QUALFCTN_POSESN_PSN_CNT: Int = 0,
    val TOT_PSN_CNT: Int = 0,
    val REFINE_LOTNO_ADDR: String = "",
    val REFINE_ROADNM_ADDR: String = "",
    val REFINE_ZIP_CD: String = "",
    val REFINE_WGS84_LOGT: String? = "",
    val REFINE_WGS84_LAT: String? = ""
)
