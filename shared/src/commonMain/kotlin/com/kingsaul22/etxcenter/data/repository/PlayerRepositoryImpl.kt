package com.kingsaul22.etxcenter.data.repository

import com.kingsaul22.etxcenter.data.dto.PlayerDto
import com.kingsaul22.etxcenter.data.dto.PlayerStatsDto
import com.kingsaul22.etxcenter.data.mapper.toDomain
import com.kingsaul22.etxcenter.domain.model.Player
import com.kingsaul22.etxcenter.domain.model.PlayerStats
import com.kingsaul22.etxcenter.domain.repository.IPlayerRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerRepositoryImpl(
    private val database: FirebaseDatabase
) : IPlayerRepository {

    override fun getPlayersFlow(): Flow<List<Player>> {
        // Escuchamos el nodo "players" en Firebase
        return database.reference("players").valueEvents.map { dataSnapshot ->
            // Si el nodo no existe o está vacío, devolvemos lista vacía
            if (!dataSnapshot.exists) return@map emptyList()

            // Firebase devuelve un mapa donde la clave es el ID del jugador y el valor es el objeto JSON (PlayerDto)
            val children = dataSnapshot.children
            children.mapNotNull { childSnapshot ->
                try {
                    val id = childSnapshot.key ?: return@mapNotNull null
                    val dto = childSnapshot.value<PlayerDto>()
                    dto.toDomain(id = id)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null // Si un jugador falla, lo ignoramos y seguimos
                }
            }
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
    }
}