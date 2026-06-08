package com.kingsaul22.etxcenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveStateDto(
    @SerialName("arena") val arena: String = "cs_p",
    @SerialName("has_winner") val hasWinner: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = false,
    @SerialName("is_overtime") val isOvertime: Boolean = false,
    @SerialName("is_replay") val isReplay: Boolean = false,
    @SerialName("score") val score: ScoreDto = ScoreDto(),
    @SerialName("time_remaining_seconds") val timeRemainingSeconds: Long = 0,
    @SerialName("winner") val winner: String = "",
)
