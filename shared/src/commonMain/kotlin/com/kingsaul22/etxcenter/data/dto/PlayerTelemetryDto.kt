package com.kingsaul22.etxcenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerTelemetryDto(
    @SerialName("assists") val assists: Int = 0,
    @SerialName("boost") val boost: Int = 0, // Defaults to 0 safely for bots like Middy
    @SerialName("demos") val demos: Int = 0,
    @SerialName("goals") val goals: Int = 0,
    @SerialName("saves") val saves: Int = 0,
    @SerialName("score") val score: Int = 0,
    @SerialName("shots") val shots: Int = 0,
    @SerialName("team") val team: Int = 0, // 0 = Blue, 1 = Orange
    @SerialName("touches") val touches: Int = 0
)
