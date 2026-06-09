package com.kingsaul22.etxcenter.data.repository

import com.kingsaul22.etxcenter.data.dto.PlayerDto
import com.kingsaul22.etxcenter.data.dto.PlayerStatsDto
import com.kingsaul22.etxcenter.data.mapper.toDomain
import com.kingsaul22.etxcenter.domain.model.Player
import com.kingsaul22.etxcenter.domain.model.PlayerStats
import com.kingsaul22.etxcenter.domain.repository.IPlayerRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry

class PlayerRepositoryImpl(
    private val database: FirebaseDatabase
) : IPlayerRepository {

    override fun getPlayersFlow(): Flow<List<Player>> {
        return database.reference("players").valueEvents.map { dataSnapshot ->
            if (!dataSnapshot.exists) return@map emptyList()

            val children = dataSnapshot.children
            children.mapNotNull { childSnapshot ->
                try {
                    val id = childSnapshot.key ?: return@mapNotNull null
                    val dto = childSnapshot.value<PlayerDto>()
                    dto.toDomain(id = id)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
        .flowOn(Dispatchers.Default)
        .retry(3) { e ->
            e.printStackTrace()
            delay(1000)
            true
        }
        .catch { e ->
            e.printStackTrace()
            emit(emptyList())
        }
    }

    override fun getPlayerStatsFlow(): Flow<List<PlayerStats>> {
        return database.reference("stats_cumulative").valueEvents.map { snapshot ->
            if (!snapshot.exists) return@map emptyList()
            snapshot.children.mapNotNull { child ->
                try {
                    val id = child.key ?: return@mapNotNull null
                    child.value<PlayerStatsDto>().toDomain(id)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
        .flowOn(Dispatchers.Default)
        .retry(3) { e ->
            e.printStackTrace()
            delay(1000)
            true
        }
        .catch { e ->
            e.printStackTrace()
            emit(emptyList())
        }
    }

    override suspend fun createPlayer(id: String, displayName: String, teamId: String?): Result<Unit> {
        return try {
            val playerId = id.trim()
            val name = displayName.trim()
            if (playerId.isBlank() || name.isBlank()) {
                return Result.failure(Exception("Player ID and Display Name cannot be blank"))
            }

            // 1. Create player record
            database.reference("players/$playerId").setValue(PlayerDto(displayName = name, teamId = teamId))

            // 2. Initialize empty cumulative stats
            database.reference("stats_cumulative/$playerId").setValue(PlayerStatsDto())

            // 3. Add to team roster if assigned
            if (teamId != null) {
                database.reference("teams/$teamId/roster/$playerId").setValue(true)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun updatePlayer(id: String, displayName: String, teamId: String?): Result<Unit> {
        return try {
            val playerId = id.trim()
            val name = displayName.trim()
            if (playerId.isBlank() || name.isBlank()) {
                return Result.failure(Exception("Player ID and Display Name cannot be blank"))
            }

            // Get old teamId for roster cleanup
            val snapshot = database.reference("players/$playerId").valueEvents.first()
            val oldTeamId = if (snapshot.exists) {
                try {
                    snapshot.child("team_id").value<String?>()
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }

            // 1. Update player details
            database.reference("players/$playerId").setValue(PlayerDto(displayName = name, teamId = teamId))

            // 2. Reconcile rosters if team changed
            if (oldTeamId != teamId) {
                if (oldTeamId != null) {
                    database.reference("teams/$oldTeamId/roster/$playerId").removeValue()
                }
                if (teamId != null) {
                    database.reference("teams/$teamId/roster/$playerId").setValue(true)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun deletePlayer(id: String): Result<Unit> {
        return try {
            val playerId = id.trim()
            // Get current teamId to clean up roster
            val snapshot = database.reference("players/$playerId").valueEvents.first()
            val teamId = if (snapshot.exists) {
                try {
                    snapshot.child("team_id").value<String?>()
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }

            // 1. Remove from team roster
            if (teamId != null) {
                database.reference("teams/$teamId/roster/$playerId").removeValue()
            }

            // 2. Remove player record
            database.reference("players/$playerId").removeValue()

            // 3. Remove cumulative stats
            database.reference("stats_cumulative/$playerId").removeValue()

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}