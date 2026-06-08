package com.kingsaul22.etxcenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveEventDto(
    val type: String,
    @SerialName("timestamp_ms") val timestampMs: Long,
    @SerialName("game_seconds_remaining") val gameSecondsRemaining: Long? = null,
    @SerialName("pre_hit_speed") val preHitSpeed: Int? = null,
    @SerialName("post_hit_speed") val postHitSpeed: Int? = null
)
