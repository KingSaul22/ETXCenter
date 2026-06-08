package com.kingsaul22.etxcenter.data.mapper

import com.kingsaul22.etxcenter.data.dto.CalendarEntryDto
import com.kingsaul22.etxcenter.domain.model.CalendarEntry

fun CalendarEntryDto.toDomain(id: String): CalendarEntry = CalendarEntry(
    id = id,
    blueTeamId = blueTeamId,
    orangeTeamId = orangeTeamId,
    dateTimeWindow = dateTimeWindow,
    matchIds = matches.keys.toList()
)
