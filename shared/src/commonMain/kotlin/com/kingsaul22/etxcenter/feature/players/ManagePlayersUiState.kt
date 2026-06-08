package com.kingsaul22.etxcenter.feature.players

import com.kingsaul22.etxcenter.domain.model.Player
import com.kingsaul22.etxcenter.domain.model.Team

data class ManagePlayersUiState(
    val players: List<Player> = emptyList(),
    val teams: List<Team> = emptyList(),
    val isLoading: Boolean = false
)
