package com.kingsaul22.etxcenter.domain.model

data class PlayerTelemetry(
    val playerId: String,
    val assists: Int,
    val boost: Int,
    val demos: Int,
    val goals: Int,
    val saves: Int,
    val score: Int,
    val shots: Int,
    val team: Int, // 0 = Blue, 1 = Orange
    val touches: Int
)
