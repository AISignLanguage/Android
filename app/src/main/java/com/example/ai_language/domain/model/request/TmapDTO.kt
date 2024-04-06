package com.example.ai_language.domain.model.request

data class TmapDTO(
val startX: Double,
val startY: Double,
val endX: Double,
val endY: Double,
val startName: String? = null,
val endName: String? = null
)
