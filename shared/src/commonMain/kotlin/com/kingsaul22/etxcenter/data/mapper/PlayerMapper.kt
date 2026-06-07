package com.kingsaul22.etxcenter.data.mapper

import com.kingsaul22.etxcenter.data.dto.PlayerDto
import com.kingsaul22.etxcenter.domain.model.Player

// Convertimos el DTO en Dominio, inyectando el ID (que Firebase da como clave del nodo)
fun PlayerDto.toDomain(id: String): Player {
    return Player(
        id = id,
        displayName = this.displayName,
        teamId = this.teamId
    )
}