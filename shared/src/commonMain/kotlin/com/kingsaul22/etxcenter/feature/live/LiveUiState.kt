package com.kingsaul22.etxcenter.feature.live

sealed interface LiveUiState {
    data object Loading : LiveUiState
    data object NoActiveMatch : LiveUiState
    data class ActiveMatch(
        val scoreBlue: Int,
        val scoreOrange: Int,
        val timeRemainingSeconds: Long
    ) : LiveUiState
}
