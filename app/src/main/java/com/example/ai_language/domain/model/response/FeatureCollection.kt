package com.example.ai_language.domain.model.response

import com.google.gson.annotations.SerializedName

data class FeatureCollection(
    val type: String = "",
    val features: List<Feature> = emptyList()
)

data class Feature(
    val type: String = "",
    val geometry: Geometry = Geometry(),
    val properties: Properties = Properties()
)

data class Geometry(
    val type: String = "",
    @SerializedName("coordinates")
    private val rawCoordinates: Any = Any()
) {
    val coordinates: Any
        get() = when (type) {
            "Point" -> {
                val pointList = rawCoordinates as? List<*>
                pointList?.map { it.toString().toDoubleOrNull() ?: 0.0 } ?: emptyList()
            }
            "LineString" -> {
                val lineStringList = rawCoordinates as? List<*>
                lineStringList?.map { rawPoint ->
                    val rawPointList = rawPoint as? List<*>
                    rawPointList?.map { it.toString().toDoubleOrNull() ?: 0.0 } ?: emptyList()
                } ?: emptyList()
            }
            else -> emptyList()
        }
}

data class Properties(
    val totalDistance: Int = 0,
    val totalTime: Int = 0,
    val index: Int = 0,
    val pointIndex: Int = 0,
    val name: String = "",
    var description: String = "",
    val direction: String = "",
    val nearPoiName: String = "",
    val nearPoiX: String = "",
    val nearPoiY: String = "",
    val intersectionName: String = "",
    val facilityType: String = "",
    val facilityName: String = "",
    val turnType: Int = 0,
    val pointType: String = "",
    var time: String? = "",
    var distance : String? = ""
)
