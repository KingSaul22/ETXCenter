package com.kingsaul22.etxcenter.domain.repository

import com.kingsaul22.etxcenter.domain.model.Player
import com.kingsaul22.etxcenter.domain.model.PlayerStats
import kotlinx.coroutines.flow.Flow

interface IPlayerRepository {
    // Retorna un flujo de datos que se actualizará automáticamente si hay cambios en Firebase
    fun getPlayersFlow(): Flow<List<Player>>

    fun getPlayerStatsFlow(): Flow<List<PlayerStats>>

    suspend fun createPlayer(id: String, displayName: String, teamId: String?): Result<Unit>

    suspend fun updatePlayer(id: String, displayName: String, teamId: String?): Result<Unit>

    suspend fun deletePlayer(id: String): Result<Unit>
}