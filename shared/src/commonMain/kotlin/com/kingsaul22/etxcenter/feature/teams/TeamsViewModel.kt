package com.kingsaul22.etxcenter.feature.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class TeamsViewModel(
    private val teamRepository: ITeamRepository
) : ViewModel() {

    val uiState: StateFlow<TeamsUiState> = combine(
        teamRepository.getTeamsFlow(),
        teamRepository.getTeamStatsFlow()
    ) { teams, stats ->
        val profiles = teams.map { team ->
            val stat = stats.find { it.teamId == team.id }
            val matchesPlayed = stat?.matchesPlayed ?: 0
            val wins = stat?.wins ?: 0
            val winRate = if (matchesPlayed > 0) (wins * 100 / matchesPlayed) else 0

            TeamProfile(
                teamId = team.id,
                name = team.name,
                logoUrl = team.logoUrl,
                matchesPlayed = matchesPlayed,
                wins = wins,
                losses = stat?.losses ?: 0,
                goalsFor = stat?.goalsFor ?: 0,
                goalsAgainst = stat?.goalsAgainst ?: 0,
                demos = stat?.demos ?: 0,
                winRatePercentage = winRate
            )
        }
        TeamsUiState.Success(teams = profiles)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TeamsUiState.Loading
    )
}
