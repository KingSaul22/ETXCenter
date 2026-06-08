package com.kingsaul22.etxcenter.feature.stats

sealed interface StatsUiState {
    data object Loading : StatsUiState
    data class Success(
        val matches: List<MatchProfile>
    ) : StatsUiState
}
