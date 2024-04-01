package com.example.ai_language.domain.model.response
data class ApiResponse(
    val LIST_TOTAL_COUNT: Int, // 행총건수
    val CODE: String, // 응답결과코드
    val MESSAGE: String, // 응답결과메세지
    val API_VERSION: String, // API버전
    val items: List<Point> // 실제 데이터를 담는 리스트
)

data class Point(
    val SIGUN_NM: String?, // 시군명, 선택적이므로 nullable
    val SIGUN_CD: String?, // 시군코드, 선택적이므로 nullable
    val BIZPLC_NM: String?, // 사업장명
    val LICENSG_DE: String?, // 인허가일자
    val BSN_STATE_NM: String?, // 영업상태명
    val CLSBIZ_DE: String?, // 폐업일자
    val LOCPLC_AR: String?, // 소재지면적(㎡)
    val LOCPLC_ZIP_CD: String?, // 소재지우편번호
    val ENTRNC_PSN_CAPA: Int?, // 입소정원(명)
    val QUALFCTN_POSESN_PSN_CNT: Int?, // 자격소유인원수(명)
    val TOT_PSN_CNT: Int?, // 총인원수(명)
    val REFINE_ROADNM_ADDR: String?, // 소재지도로명주소
    val REFINE_LOTNO_ADDR: String?, // 소재지지번주소
    val REFINE_ZIP_CD: String?, // 소재지우편번호(재정의)
    val REFINE_WGS84_LAT: Double?, // WGS84위도
    val REFINE_WGS84_LOGT: Double? // WGS84경도
)
