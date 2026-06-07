package com.kingsaul22.etxcenter.feature.home

data class HomeUiState(
    val isLoading: Boolean = true,
    val teamCount: Int = 0,
    val playerCount: Int = 0,
    val activeEventsCount: Int = 0,
    val errorMessage: String? = null
)
