package com.kingsaul22.etxcenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    @SerialName("display_name") val displayName: String,
    @SerialName("team_id") val teamId: String? = null
)