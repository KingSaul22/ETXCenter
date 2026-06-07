package com.kingsaul22.etxcenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScoreDto(
    @SerialName("blue") val blue: Int = 0,
    @SerialName("orange") val orange: Int = 0
)
