package com.kingsaul22.etxcenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalendarEntryDto(
    @SerialName("blue_team_id") val blueTeamId: String,
    @SerialName("orange_team_id") val orangeTeamId: String,
    @SerialName("start_time") val startTime: Long,
    @SerialName("end_time") val endTime: Long
)
