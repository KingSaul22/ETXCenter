package com.kingsaul22.etxcenter.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.IPlayerRepository
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    playerRepository: IPlayerRepository,
    teamRepository: ITeamRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        playerRepository.getPlayersFlow(),
        teamRepository.getTeamsFlow()
    ) { players, teams ->
        HomeUiState(
            isLoading = false,
            teamCount = teams.size,
            playerCount = players.size,
            activeEventsCount = 0 // We will hook up matches later
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )
}