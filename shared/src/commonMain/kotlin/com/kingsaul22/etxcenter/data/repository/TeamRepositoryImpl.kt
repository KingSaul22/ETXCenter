package com.kingsaul22.etxcenter.data.repository

import com.kingsaul22.etxcenter.data.dto.TeamDto
import com.kingsaul22.etxcenter.data.dto.TeamStatsDto
import com.kingsaul22.etxcenter.data.mapper.toDomain
import com.kingsaul22.etxcenter.domain.model.Team
import com.kingsaul22.etxcenter.domain.model.TeamStats
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry

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
        .retry { e ->
            e.printStackTrace()
            delay(1000)
            true
        }
        .catch { e ->
            e.printStackTrace()
            emit(emptyList())
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
        .retry { e ->
            e.printStackTrace()
            delay(1000)
            true
        }
        .catch { e ->
            e.printStackTrace()
            emit(emptyList())
        }
    }

    override suspend fun createTeam(name: String, logoUrl: String?): Result<Unit> {
        return try {
            val teamRef = database.reference("teams").push()
            val teamId = teamRef.key ?: return Result.failure(Exception("Failed to generate team ID"))
            teamRef.setValue(TeamDto(name = name, logoUrl = logoUrl))
            database.reference("stats_cumulative_teams/$teamId").setValue(TeamStatsDto())
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun deleteTeam(teamId: String): Result<Unit> {
        return try {
            database.reference("teams/$teamId").removeValue()
            database.reference("stats_cumulative_teams/$teamId").removeValue()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}