package com.kingsaul22.etxcenter.domain.model

import kotlin.time.Instant

data class LiveEvent(
    val id: String,
    val type: String,
    val timestamp: Instant,
    val data: Map<String, String>
)
