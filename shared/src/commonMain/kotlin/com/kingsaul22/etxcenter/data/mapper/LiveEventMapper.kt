package com.kingsaul22.etxcenter.data.mapper

import com.kingsaul22.etxcenter.data.dto.LiveEventDto
import com.kingsaul22.etxcenter.domain.model.LiveEvent
import kotlin.time.Instant

fun LiveEventDto.toDomain(id: String): LiveEvent {
    val payload = buildMap {
        gameSecondsRemaining?.let { put("game_seconds_remaining", it.toString()) }
        preHitSpeed?.let { put("pre_hit_speed", it.toString()) }
        postHitSpeed?.let { put("post_hit_speed", it.toString()) }
    }
    return LiveEvent(
        id = id,
        type = this.type,
        timestamp = Instant.fromEpochMilliseconds(this.timestampMs),
        data = payload
    )
}
