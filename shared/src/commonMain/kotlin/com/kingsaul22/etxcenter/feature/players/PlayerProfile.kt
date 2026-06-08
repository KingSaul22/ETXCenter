package com.kingsaul22.etxcenter.feature.players

data class PlayerProfile(
    val playerId: String,
    val displayName: String,
    val teamId: String?,
    val goals: Int,
    val assists: Int,
    val saves: Int,
    val mvps: Int,
    val wins: Int,
    val losses: Int
)
