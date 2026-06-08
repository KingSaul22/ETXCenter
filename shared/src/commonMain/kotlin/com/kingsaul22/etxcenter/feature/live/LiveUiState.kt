package com.kingsaul22.etxcenter.feature.live

import com.kingsaul22.etxcenter.domain.model.LiveEvent
import com.kingsaul22.etxcenter.domain.model.PlayerTelemetry

sealed interface LiveUiState {
    data object Loading : LiveUiState
    data object NoActiveMatch : LiveUiState
    data class ActiveMatch(
        val arena: String,
        val hasWinner: Boolean,
        val isActive: Boolean,
        val isOvertime: Boolean,
        val isReplay: Boolean,
        val scoreBlue: Int,
        val scoreOrange: Int,
        val timeRemainingSeconds: Long,
        val winner: String,
        val events: List<LiveEvent>,
        val playerTelemetry: List<PlayerTelemetry>
    ) : LiveUiState
}
