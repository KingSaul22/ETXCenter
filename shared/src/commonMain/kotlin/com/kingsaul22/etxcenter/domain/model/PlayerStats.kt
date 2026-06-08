package com.kingsaul22.etxcenter.domain.model

data class PlayerStats(
    val playerId: String,
    val score: Int,
    val goals: Int,
    val assists: Int,
    val saves: Int,
    val shots: Int,
    val mvps: Int,
    val wins: Int,
    val losses: Int
)
