package com.kingsaul22.etxcenter.feature.players

sealed interface PlayersUiState {
    data object Loading : PlayersUiState
    data class Success(
        val players: List<PlayerProfile>
    ) : PlayersUiState
}
