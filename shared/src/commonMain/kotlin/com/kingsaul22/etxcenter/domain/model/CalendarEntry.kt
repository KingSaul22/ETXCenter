package com.kingsaul22.etxcenter.domain.model

import kotlinx.datetime.Instant

data class CalendarEntry(
    val id: String,
    val blueTeamId: String,
    val orangeTeamId: String,
    val startTime: Instant,
    val endTime: Instant,
    val matchIds: List<String> = emptyList()
)
