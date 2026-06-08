package com.kingsaul22.etxcenter.domain.model

data class LiveState(
    val arena: String,
    val hasWinner: Boolean,
    val isActive: Boolean,
    val isOvertime: Boolean,
    val isReplay: Boolean,
    val scoreBlue: Int,
    val scoreOrange: Int,
    val timeRemainingSeconds: Long,
    val winner: String,
    val playerTelemetry: List<PlayerTelemetry>,
)
