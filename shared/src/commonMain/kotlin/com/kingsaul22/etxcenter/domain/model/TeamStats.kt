package com.kingsaul22.etxcenter.domain.model

data class TeamStats(
    val teamId: String,
    val matchesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val assists: Int,
    val saves: Int,
    val shots: Int,
    val demos: Int
)
