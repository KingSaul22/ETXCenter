package com.kingsaul22.etxcenter.feature.stats

import kotlin.time.Instant

data class MatchProfile(
    val matchId: String,
    val timestamp: Instant,
    val blueTeamName: String,
    val orangeTeamName: String,
    val blueScore: Long,
    val orangeScore: Long,
    val formattedDate: String
)
