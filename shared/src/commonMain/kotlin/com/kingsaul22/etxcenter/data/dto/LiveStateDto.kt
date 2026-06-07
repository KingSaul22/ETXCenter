package com.kingsaul22.etxcenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveStateDto(
    @SerialName("score") val score: ScoreDto = ScoreDto(),
    @SerialName("time_remaining_seconds") val timeRemainingSeconds: Long = 0
)
