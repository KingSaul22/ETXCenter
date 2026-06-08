package com.kingsaul22.etxcenter.data.mapper

import com.kingsaul22.etxcenter.data.dto.PlayerDto
import com.kingsaul22.etxcenter.data.dto.PlayerStatsDto
import com.kingsaul22.etxcenter.domain.model.Player
import com.kingsaul22.etxcenter.domain.model.PlayerStats

// Convertimos el DTO en Dominio, inyectando el ID (que Firebase da como clave del nodo)
fun PlayerDto.toDomain(id: String): Player {
    return Player(
        id = id,
        displayName = this.displayName,
        teamId = this.teamId
    )
}

fun PlayerStatsDto.toDomain(id: String): PlayerStats {
    return PlayerStats(
        playerId = id,
        score = this.score,
        goals = this.goals,
        assists = this.assists,
        saves = this.saves,
        shots = this.shots,
        mvps = this.mvps,
        wins = this.wins,
        losses = this.losses
    )
}