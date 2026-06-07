package com.kingsaul22.etxcenter.feature.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.ILiveRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LiveViewModel(
    private val liveRepository: ILiveRepository
) : ViewModel() {

    val uiState: StateFlow<LiveUiState> = liveRepository
        .getLiveStateFlow()
        .map { liveState ->
            when (liveState) {
                null -> LiveUiState.NoActiveMatch
                else -> LiveUiState.ActiveMatch(
                    scoreBlue = liveState.scoreBlue,
                    scoreOrange = liveState.scoreOrange,
                    timeRemainingSeconds = liveState.timeRemainingSeconds
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LiveUiState.Loading
        )
}
