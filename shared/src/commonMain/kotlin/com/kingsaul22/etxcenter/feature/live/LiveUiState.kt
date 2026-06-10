package com.kingsaul22.etxcenter.feature.live

import com.kingsaul22.etxcenter.domain.model.LiveEvent
import com.kingsaul22.etxcenter.domain.model.PlayerTelemetry

sealed interface LiveUiState {
    val isStreamExpanded: Boolean

    data class Loading(override val isStreamExpanded: Boolean = false) : LiveUiState
    data class NoActiveMatch(override val isStreamExpanded: Boolean = false) : LiveUiState
    data class ActiveMatch(
        override val isStreamExpanded: Boolean = false,
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
