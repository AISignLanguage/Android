package com.example.ai_language.domain.model.response

data class ResultPath(
    val route : Result_trackoption = Result_trackoption(),
    val message : String = "",
    val code : Int = 0
)
data class Result_trackoption(
    val traoptimal : List<Result_path> = emptyList()
)
data class Result_path(
    val summary : Result_distance = Result_distance() ,
    val path : List<List<Double>> = emptyList()
)
data class Result_distance(
    val distance : Int = 0
)
