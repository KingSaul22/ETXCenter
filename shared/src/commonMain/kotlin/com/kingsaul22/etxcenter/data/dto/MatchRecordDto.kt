package com.kingsaul22.etxcenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchRecordDto(
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("blue_score") val blueScore: Long,
    @SerialName("orange_score") val orangeScore: Long,
    @SerialName("match_id") val matchId: String,
    @SerialName("blue_team_id") val blueTeamId: String? = null,
    @SerialName("blue_shots") val blueShots: Long = 0,
    @SerialName("blue_saves") val blueSaves: Long = 0,
    @SerialName("blue_assists") val blueAssists: Long = 0,
    @SerialName("blue_demos") val blueDemos: Long = 0,
    @SerialName("orange_team_id") val orangeTeamId: String? = null,
    @SerialName("orange_shots") val orangeShots: Long = 0,
    @SerialName("orange_saves") val orangeSaves: Long = 0,
    @SerialName("orange_assists") val orangeAssists: Long = 0,
    @SerialName("orange_demos") val orangeDemos: Long = 0
)
