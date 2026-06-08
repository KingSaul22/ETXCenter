package com.kingsaul22.etxcenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerStatsDto(
    @SerialName("score") val score: Int = 0,
    @SerialName("goals") val goals: Int = 0,
    @SerialName("assists") val assists: Int = 0,
    @SerialName("saves") val saves: Int = 0,
    @SerialName("shots") val shots: Int = 0,
    @SerialName("mvps") val mvps: Int = 0,
    @SerialName("wins") val wins: Int = 0,
    @SerialName("losses") val losses: Int = 0,
)
