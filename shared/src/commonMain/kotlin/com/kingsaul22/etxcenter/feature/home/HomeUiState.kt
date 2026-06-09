package com.kingsaul22.etxcenter.feature.home

enum class HomeMatchStatus {
    SCHEDULED,
    ACTIVE
}

data class HomeCalendarEntry(
    val id: String,
    val blueTeamName: String,
    val blueTeamLogoUrl: String?,
    val orangeTeamName: String,
    val orangeTeamLogoUrl: String?,
    val startTimeEpoch: Long,
    val endTimeEpoch: Long,
    val status: HomeMatchStatus,
    val gamesPlayed: Int
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val teamCount: Int = 0,
    val playerCount: Int = 0,
    val activeEventsCount: Int = 0,
    val isAdmin: Boolean = false,
    val upcomingMatches: List<HomeCalendarEntry> = emptyList(),
    val errorMessage: String? = null
)
