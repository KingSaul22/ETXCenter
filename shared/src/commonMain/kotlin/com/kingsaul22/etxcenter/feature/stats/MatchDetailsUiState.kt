package com.kingsaul22.etxcenter.feature.stats

sealed interface MatchDetailsUiState {
    data object Loading : MatchDetailsUiState
    data object NotFound : MatchDetailsUiState
    data class Success(
        val matchId: String,
        val formattedDate: String,
        val blueTeamName: String,
        val orangeTeamName: String,
        val blueScore: Long,
        val orangeScore: Long,
        val blueShots: Long,
        val orangeShots: Long,
        val blueSaves: Long,
        val orangeSaves: Long,
        val blueAssists: Long,
        val orangeAssists: Long,
        val blueDemos: Long,
        val orangeDemos: Long
    ) : MatchDetailsUiState
}
