package com.kingsaul22.etxcenter.data.mapper

import com.kingsaul22.etxcenter.data.dto.LiveStateDto
import com.kingsaul22.etxcenter.domain.model.LiveState
import com.kingsaul22.etxcenter.domain.model.PlayerTelemetry

fun LiveStateDto.toDomain(): LiveState {
    return LiveState(
        arena = this.arena,
        hasWinner = this.hasWinner,
        isActive = this.isActive,
        isOvertime = this.isOvertime,
        isReplay = this.isReplay,
        scoreBlue = this.score.blue,
        scoreOrange = this.score.orange,
        timeRemainingSeconds = this.timeRemainingSeconds,
        winner = this.winner,
        // Transform Map<String, Dto> into List<Domain>
        playerTelemetry = this.playerTelemetry.map { (keyId, dto) ->
            PlayerTelemetry(
                playerId = keyId, // Inject the key as the ID
                assists = dto.assists,
                boost = dto.boost,
                demos = dto.demos,
                goals = dto.goals,
                saves = dto.saves,
                score = dto.score,
                shots = dto.shots,
                team = dto.team,
                touches = dto.touches
            )
        },
    )
}
