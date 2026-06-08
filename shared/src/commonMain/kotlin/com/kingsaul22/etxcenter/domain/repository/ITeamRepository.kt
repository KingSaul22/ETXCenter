package com.kingsaul22.etxcenter.domain.repository

import com.kingsaul22.etxcenter.domain.model.Team
import com.kingsaul22.etxcenter.domain.model.TeamStats
import kotlinx.coroutines.flow.Flow

interface ITeamRepository {
    fun getTeamsFlow(): Flow<List<Team>>
    
    fun getTeamStatsFlow(): Flow<List<TeamStats>>

    suspend fun createTeam(name: String, logoUrl: String?): Result<Unit>

    suspend fun deleteTeam(teamId: String): Result<Unit>

    suspend fun updateTeamRoster(teamId: String, playerIds: List<String>): Result<Unit>
}