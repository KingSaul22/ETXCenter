package com.kingsaul22.etxcenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamStatsDto(
    @SerialName("matches_played") val matchesPlayed: Int = 0,
    @SerialName("wins") val wins: Int = 0,
    @SerialName("losses") val losses: Int = 0,
    @SerialName("goals_for") val goalsFor: Int = 0,
    @SerialName("goals_against") val goalsAgainst: Int = 0,
    @SerialName("assists") val assists: Int = 0,
    @SerialName("saves") val saves: Int = 0,
    @SerialName("shots") val shots: Int = 0,
    @SerialName("demos") val demos: Int = 0,
)
