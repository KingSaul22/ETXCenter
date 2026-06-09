package com.kingsaul22.etxcenter.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.IAuthRepository
import com.kingsaul22.etxcenter.domain.repository.IPlayerRepository
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import com.kingsaul22.etxcenter.domain.repository.ICalendarRepository
import com.kingsaul22.etxcenter.domain.repository.IMatchRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock

class HomeViewModel(
    private val playerRepository: IPlayerRepository,
    private val teamRepository: ITeamRepository,
    private val authRepository: IAuthRepository,
    private val calendarRepository: ICalendarRepository,
    private val matchRepository: IMatchRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        playerRepository.getPlayersFlow(),
        teamRepository.getTeamsFlow(),
        authRepository.getIsAdminFlow(),
        calendarRepository.getCalendarFlow(),
        matchRepository.getMatchesFlow(limit = 100)
    ) { players, teams, isAdmin, entries, matches ->
        val currentTime = Clock.System.now().epochSeconds
        
        // Filter: only show matches whose scheduled end time is in the future
        val upcoming = entries.filter { it.endTime.epochSeconds > currentTime }
            .map { entry ->
                val blueTeam = teams.find { it.id == entry.blueTeamId }
                val orangeTeam = teams.find { it.id == entry.orangeTeamId }
                
                // Match the played matches in matches_index to see if games have started
                val matchedMatches = matches.filter { match ->
                    val matchTime = match.timestamp.epochSeconds
                    val inTimeWindow = matchTime in entry.startTime.epochSeconds..entry.endTime.epochSeconds
                    val sameTeams = (match.blueTeamId == entry.blueTeamId && match.orangeTeamId == entry.orangeTeamId) ||
                            (match.blueTeamId == entry.orangeTeamId && match.orangeTeamId == entry.blueTeamId)
                    inTimeWindow && sameTeams
                }
                
                val status = when {
                    currentTime >= entry.startTime.epochSeconds -> HomeMatchStatus.ACTIVE
                    else -> HomeMatchStatus.SCHEDULED
                }
                
                HomeCalendarEntry(
                    id = entry.id,
                    blueTeamName = blueTeam?.name ?: entry.blueTeamId,
                    blueTeamLogoUrl = blueTeam?.logoUrl,
                    orangeTeamName = orangeTeam?.name ?: entry.orangeTeamId,
                    orangeTeamLogoUrl = orangeTeam?.logoUrl,
                    startTimeEpoch = entry.startTime.epochSeconds,
                    endTimeEpoch = entry.endTime.epochSeconds,
                    status = status,
                    gamesPlayed = matchedMatches.size
                )
            }.sortedBy { it.startTimeEpoch }

        HomeUiState(
            isLoading = false,
            teamCount = teams.size,
            playerCount = players.size,
            activeEventsCount = 0,
            isAdmin = isAdmin,
            upcomingMatches = upcoming
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun signOutAdmin() {
        viewModelScope.launch {
            authRepository.signOutAdmin()
        }
    }
}