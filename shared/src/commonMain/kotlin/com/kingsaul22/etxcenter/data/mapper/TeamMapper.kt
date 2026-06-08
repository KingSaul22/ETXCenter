package com.kingsaul22.etxcenter.data.mapper

import com.kingsaul22.etxcenter.data.dto.TeamDto
import com.kingsaul22.etxcenter.data.dto.TeamStatsDto
import com.kingsaul22.etxcenter.domain.model.Team
import com.kingsaul22.etxcenter.domain.model.TeamStats

// Convertimos el DTO en Dominio, inyectando el ID (que Firebase da como clave del nodo)
fun TeamDto.toDomain(id: String): Team {
    return Team(
        id = id,
        name = this.name,
        logoUrl = this.logoUrl
    )
}

fun TeamStatsDto.toDomain(id: String): TeamStats {
    return TeamStats(
        teamId = id,
        matchesPlayed = this.matchesPlayed,
        wins = this.wins,
        losses = this.losses,
        goalsFor = this.goalsFor,
        goalsAgainst = this.goalsAgainst,
        assists = this.assists,
        saves = this.saves,
        shots = this.shots,
        demos = this.demos
    )
}