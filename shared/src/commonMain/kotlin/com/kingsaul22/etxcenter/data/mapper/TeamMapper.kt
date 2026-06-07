package com.kingsaul22.etxcenter.data.mapper

import com.kingsaul22.etxcenter.data.dto.TeamDto
import com.kingsaul22.etxcenter.domain.model.Team

// Convertimos el DTO en Dominio, inyectando el ID (que Firebase da como clave del nodo)
fun TeamDto.toDomain(id: String): Team {
    return Team(
        id = id,
        name = this.name,
        logoUrl = this.logoUrl
    )
}