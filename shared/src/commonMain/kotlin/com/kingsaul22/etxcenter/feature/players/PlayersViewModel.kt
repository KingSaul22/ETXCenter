package com.kingsaul22.etxcenter.feature.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.IPlayerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PlayersViewModel(
    private val playerRepository: IPlayerRepository
) : ViewModel() {

    val uiState: StateFlow<PlayersUiState> = combine(
        playerRepository.getPlayersFlow(),
        playerRepository.getPlayerStatsFlow()
    ) { players, stats ->
        val profiles = players.map { player ->
            val stat = stats.find { it.playerId == player.id }
            PlayerProfile(
                playerId = player.id,
                displayName = player.displayName,
                teamId = player.teamId,
                goals = stat?.goals ?: 0,
                assists = stat?.assists ?: 0,
                saves = stat?.saves ?: 0,
                mvps = stat?.mvps ?: 0,
                wins = stat?.wins ?: 0,
                losses = stat?.losses ?: 0
            )
        }
        PlayersUiState.Success(players = profiles)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlayersUiState.Loading
    )
}
