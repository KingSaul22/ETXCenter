package com.kingsaul22.etxcenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    @SerialName("name") val name: String,
    @SerialName("logo_url") val logoUrl: String? = null,
)
