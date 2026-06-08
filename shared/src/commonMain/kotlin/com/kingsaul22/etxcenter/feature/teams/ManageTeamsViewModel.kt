package com.kingsaul22.etxcenter.feature.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.model.Team
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageTeamsViewModel(
    private val teamRepository: ITeamRepository
) : ViewModel() {

    val teams: StateFlow<List<Team>> = teamRepository.getTeamsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createTeam(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            teamRepository.createTeam(name, null)
        }
    }

    fun deleteTeam(teamId: String) {
        viewModelScope.launch {
            teamRepository.deleteTeam(teamId)
        }
    }
}
