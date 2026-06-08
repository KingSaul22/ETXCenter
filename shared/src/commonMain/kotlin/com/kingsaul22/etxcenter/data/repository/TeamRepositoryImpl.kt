package com.kingsaul22.etxcenter.data.repository

import com.kingsaul22.etxcenter.data.dto.TeamDto
import com.kingsaul22.etxcenter.data.dto.TeamStatsDto
import com.kingsaul22.etxcenter.data.mapper.toDomain
import com.kingsaul22.etxcenter.domain.model.Team
import com.kingsaul22.etxcenter.domain.model.TeamStats
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TeamRepositoryImpl(
    private val database: FirebaseDatabase
) : ITeamRepository {

    override fun getTeamsFlow(): Flow<List<Team>> {
        return database.reference("teams").valueEvents.map { dataSnapshot ->
            if (!dataSnapshot.exists) return@map emptyList()

            dataSnapshot.children.mapNotNull { childSnapshot ->
                try {
                    val id = childSnapshot.key ?: return@mapNotNull null
                    val dto = childSnapshot.value<TeamDto>()
                    dto.toDomain(id = id)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    override fun getTeamStatsFlow(): Flow<List<TeamStats>> {
        return database.reference("stats_cumulative_teams").valueEvents.map { snapshot ->
            if (!snapshot.exists) return@map emptyList()
            snapshot.children.mapNotNull { child ->
                try {
                    val id = child.key ?: return@mapNotNull null
                    child.value<TeamStatsDto>().toDomain(id)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }
}