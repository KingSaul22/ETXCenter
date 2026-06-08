package com.kingsaul22.etxcenter.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.IAuthRepository
import com.kingsaul22.etxcenter.domain.repository.IPlayerRepository
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val playerRepository: IPlayerRepository,
    private val teamRepository: ITeamRepository,
    private val authRepository: IAuthRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        playerRepository.getPlayersFlow(),
        teamRepository.getTeamsFlow(),
        authRepository.getIsAdminFlow()
    ) { players, teams, isAdmin ->
        HomeUiState(
            isLoading = false,
            teamCount = teams.size,
            playerCount = players.size,
            activeEventsCount = 0,
            isAdmin = isAdmin
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