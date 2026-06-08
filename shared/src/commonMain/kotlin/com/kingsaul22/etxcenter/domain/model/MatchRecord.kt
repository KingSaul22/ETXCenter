package com.kingsaul22.etxcenter.domain.model

import kotlin.time.Instant

data class MatchRecord(
    val matchId: String,
    val timestamp: Instant,
    val blueScore: Long,
    val orangeScore: Long,
    val blueTeamId: String?,
    val orangeTeamId: String?,
    val blueShots: Long,
    val blueSaves: Long,
    val blueAssists: Long,
    val blueDemos: Long,
    val orangeShots: Long,
    val orangeSaves: Long,
    val orangeAssists: Long,
    val orangeDemos: Long
)
