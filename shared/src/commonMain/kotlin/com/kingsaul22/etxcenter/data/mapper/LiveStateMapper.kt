package com.kingsaul22.etxcenter.data.mapper

import com.kingsaul22.etxcenter.data.dto.LiveStateDto
import com.kingsaul22.etxcenter.domain.model.LiveState

fun LiveStateDto.toDomain(): LiveState {
    return LiveState(
        scoreBlue = this.score.blue,
        scoreOrange = this.score.orange,
        timeRemainingSeconds = this.timeRemainingSeconds
    )
}
