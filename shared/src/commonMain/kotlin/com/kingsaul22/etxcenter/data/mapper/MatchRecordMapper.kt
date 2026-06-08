package com.kingsaul22.etxcenter.data.mapper

import com.kingsaul22.etxcenter.data.dto.MatchRecordDto
import com.kingsaul22.etxcenter.domain.model.MatchRecord
import kotlin.time.Instant

fun MatchRecordDto.toDomain(): MatchRecord {
    return MatchRecord(
        matchId = this.matchId,
        timestamp = Instant.fromEpochSeconds(this.timestamp),
        blueScore = this.blueScore,
        orangeScore = this.orangeScore,
        blueTeamId = this.blueTeamId,
        orangeTeamId = this.orangeTeamId,
        blueShots = this.blueShots,
        blueSaves = this.blueSaves,
        blueAssists = this.blueAssists,
        blueDemos = this.blueDemos,
        orangeShots = this.orangeShots,
        orangeSaves = this.orangeSaves,
        orangeAssists = this.orangeAssists,
        orangeDemos = this.orangeDemos
    )
}
