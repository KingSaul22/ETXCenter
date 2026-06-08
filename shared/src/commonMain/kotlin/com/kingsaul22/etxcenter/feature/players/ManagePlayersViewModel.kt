package com.kingsaul22.etxcenter.feature.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.IPlayerRepository
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManagePlayersViewModel(
    private val playerRepository: IPlayerRepository,
    private val teamRepository: ITeamRepository
) : ViewModel() {

    val uiState: StateFlow<ManagePlayersUiState> = combine(
        playerRepository.getPlayersFlow(),
        teamRepository.getTeamsFlow()
    ) { players, teams ->
        ManagePlayersUiState(
            players = players,
            teams = teams,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ManagePlayersUiState(isLoading = true)
    )

    fun createPlayer(id: String, displayName: String, teamId: String?) {
        viewModelScope.launch {
            playerRepository.createPlayer(id, displayName, teamId)
        }
    }

    fun updatePlayer(id: String, displayName: String, teamId: String?) {
        viewModelScope.launch {
            playerRepository.updatePlayer(id, displayName, teamId)
        }
    }

    fun deletePlayer(id: String) {
        viewModelScope.launch {
            playerRepository.deletePlayer(id)
        }
    }
}
