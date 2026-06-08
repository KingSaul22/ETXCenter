package com.kingsaul22.etxcenter.feature.teams

data class TeamProfile(
    val teamId: String,
    val name: String,
    val logoUrl: String?,
    val matchesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val demos: Int,
    val winRatePercentage: Int
)
