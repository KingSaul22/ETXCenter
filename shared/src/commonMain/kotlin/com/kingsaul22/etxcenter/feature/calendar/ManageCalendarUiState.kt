package com.kingsaul22.etxcenter.feature.calendar

import com.kingsaul22.etxcenter.domain.model.CalendarEntry
import com.kingsaul22.etxcenter.domain.model.Team
import com.kingsaul22.etxcenter.domain.model.MatchRecord

data class ManageCalendarUiState(
    val calendarEntries: List<CalendarEntry> = emptyList(),
    val teams: List<Team> = emptyList(),
    val playedMatches: List<MatchRecord> = emptyList(),
    val isLoading: Boolean = false
)
