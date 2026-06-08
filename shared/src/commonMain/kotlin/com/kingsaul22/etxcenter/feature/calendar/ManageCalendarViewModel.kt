package com.kingsaul22.etxcenter.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.ICalendarRepository
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import com.kingsaul22.etxcenter.domain.repository.IMatchRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageCalendarViewModel(
    private val calendarRepository: ICalendarRepository,
    private val teamRepository: ITeamRepository,
    private val matchRepository: IMatchRepository
) : ViewModel() {

    val uiState: StateFlow<ManageCalendarUiState> = combine(
        calendarRepository.getCalendarFlow(),
        teamRepository.getTeamsFlow(),
        matchRepository.getMatchesFlow(limit = 100)
    ) { entries, teams, matches ->
        ManageCalendarUiState(
            calendarEntries = entries,
            teams = teams,
            playedMatches = matches,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ManageCalendarUiState(isLoading = true)
    )

    fun createCalendarEntry(blueTeamId: String, orangeTeamId: String, dateTimeWindow: String, matchIds: List<String>) {
        viewModelScope.launch {
            calendarRepository.createCalendarEntry(blueTeamId, orangeTeamId, dateTimeWindow, matchIds)
        }
    }

    fun updateCalendarEntry(id: String, blueTeamId: String, orangeTeamId: String, dateTimeWindow: String, matchIds: List<String>) {
        viewModelScope.launch {
            calendarRepository.updateCalendarEntry(id, blueTeamId, orangeTeamId, dateTimeWindow, matchIds)
        }
    }

    fun deleteCalendarEntry(id: String) {
        viewModelScope.launch {
            calendarRepository.deleteCalendarEntry(id)
        }
    }
}
