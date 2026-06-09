package com.kingsaul22.etxcenter.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.ICalendarRepository
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import com.kingsaul22.etxcenter.domain.repository.IMatchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
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
        val mappedEntries = entries.map { entry ->
            val matchedIds = matches.filter { match ->
                val matchTime = match.timestamp.epochSeconds
                val inTimeWindow = matchTime in entry.startTime.epochSeconds..entry.endTime.epochSeconds
                val sameTeams = (match.blueTeamId == entry.blueTeamId && match.orangeTeamId == entry.orangeTeamId) ||
                        (match.blueTeamId == entry.orangeTeamId && match.orangeTeamId == entry.blueTeamId)
                inTimeWindow && sameTeams
            }.map { it.matchId }

            entry.copy(matchIds = matchedIds)
        }

        ManageCalendarUiState(
            calendarEntries = mappedEntries,
            teams = teams,
            playedMatches = matches,
            isLoading = false
        )
    }
    .flowOn(Dispatchers.Default)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ManageCalendarUiState(isLoading = true)
    )

    fun createCalendarEntry(blueTeamId: String, orangeTeamId: String, startTime: Long, endTime: Long) {
        viewModelScope.launch {
            calendarRepository.createCalendarEntry(blueTeamId, orangeTeamId, startTime, endTime)
        }
    }

    fun updateCalendarEntry(id: String, blueTeamId: String, orangeTeamId: String, startTime: Long, endTime: Long) {
        viewModelScope.launch {
            calendarRepository.updateCalendarEntry(id, blueTeamId, orangeTeamId, startTime, endTime)
        }
    }

    fun deleteCalendarEntry(id: String) {
        viewModelScope.launch {
            calendarRepository.deleteCalendarEntry(id)
        }
    }
}
