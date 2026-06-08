package com.kingsaul22.etxcenter.feature.teams

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Success(
        val teams: List<TeamProfile>
    ) : TeamsUiState
}
