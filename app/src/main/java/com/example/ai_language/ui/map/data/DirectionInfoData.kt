package com.example.ai_language.ui.map.data

import com.example.ai_language.domain.model.response.Distance
import com.example.ai_language.domain.model.response.Duration
import com.example.ai_language.domain.model.response.Location
import java.util.concurrent.locks.Lock

data class DirectionInfoData(
    val arriveTime : String="", //도착시간
    val departureTime : String="", //출발시간
    val distance: String="", //거리
    val duration: String="",  //시간
    val startAddress: String="",
    val endAddress : String="", //도착지역
    val endLocation: Location= Location(0.0,0.0), //도착좌표
    val startLocation: Location= Location(0.0,0.0) //시작좌표
)
