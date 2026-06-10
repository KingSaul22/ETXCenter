package com.kingsaul22.etxcenter.feature.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.ILiveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class LiveViewModel(
    private val liveRepository: ILiveRepository
) : ViewModel() {

    private val _isStreamExpanded = MutableStateFlow(false)

    fun toggleStreamExpanded() {
        _isStreamExpanded.value = !_isStreamExpanded.value
    }

    val uiState: StateFlow<LiveUiState> = combine(
        liveRepository.getLiveStateFlow(),
        liveRepository.getLiveEventsFeedFlow(),
        _isStreamExpanded
    ) { liveState, events, isExpanded ->
        when (liveState) {
            null -> LiveUiState.NoActiveMatch(isExpanded)
            else -> LiveUiState.ActiveMatch(
                isStreamExpanded = isExpanded,
                arena = liveState.arena,
                hasWinner = liveState.hasWinner,
                isActive = liveState.isActive,
                isOvertime = liveState.isOvertime,
                isReplay = liveState.isReplay,
                scoreBlue = liveState.scoreBlue,
                scoreOrange = liveState.scoreOrange,
                timeRemainingSeconds = liveState.timeRemainingSeconds,
                winner = liveState.winner,
                events = events,
                playerTelemetry = liveState.playerTelemetry
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LiveUiState.Loading()
    )
}
