package com.kingsaul22.etxcenter.data.mapper

import com.kingsaul22.etxcenter.data.dto.CalendarEntryDto
import com.kingsaul22.etxcenter.domain.model.CalendarEntry
import kotlinx.datetime.Instant

fun CalendarEntryDto.toDomain(id: String): CalendarEntry = CalendarEntry(
    id = id,
    blueTeamId = blueTeamId,
    orangeTeamId = orangeTeamId,
    startTime = Instant.fromEpochSeconds(startTime),
    endTime = Instant.fromEpochSeconds(endTime),
    matchIds = emptyList() // populated dynamically in VM
)
