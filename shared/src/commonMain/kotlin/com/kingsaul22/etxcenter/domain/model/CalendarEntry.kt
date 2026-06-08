package com.kingsaul22.etxcenter.domain.model

data class CalendarEntry(
    val id: String,
    val blueTeamId: String,
    val orangeTeamId: String,
    val dateTimeWindow: String,
    val matchIds: List<String>
)
