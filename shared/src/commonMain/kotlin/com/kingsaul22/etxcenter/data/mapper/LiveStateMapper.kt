package com.kingsaul22.etxcenter.data.mapper

import com.kingsaul22.etxcenter.data.dto.LiveStateDto
import com.kingsaul22.etxcenter.domain.model.LiveState

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
        winner = this.winner
    )
}
