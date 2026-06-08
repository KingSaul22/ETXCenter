package com.kingsaul22.etxcenter.feature.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.IMatchRepository
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MatchDetailsViewModel(
    private val matchId: String,
    private val matchRepository: IMatchRepository,
    private val teamRepository: ITeamRepository
) : ViewModel() {

    val uiState: StateFlow<MatchDetailsUiState> = combine(
        matchRepository.getMatchByIdFlow(matchId),
        teamRepository.getTeamsFlow()
    ) { match, teams ->
        if (match == null) {
            MatchDetailsUiState.NotFound
        } else {
            val blueTeam = teams.find { it.id == match.blueTeamId }
            val orangeTeam = teams.find { it.id == match.orangeTeamId }

            val blueName = blueTeam?.name ?: match.blueTeamId ?: "Unknown Team"
            val orangeName = orangeTeam?.name ?: match.orangeTeamId ?: "Unknown Team"

            val dt = match.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
            val monthAbbrev = when (dt.month.name) {
                "JANUARY" -> "Jan"
                "FEBRUARY" -> "Feb"
                "MARCH" -> "Mar"
                "APRIL" -> "Apr"
                "MAY" -> "May"
                "JUNE" -> "Jun"
                "JULY" -> "Jul"
                "AUGUST" -> "Aug"
                "SEPTEMBER" -> "Sep"
                "OCTOBER" -> "Oct"
                "NOVEMBER" -> "Nov"
                "DECEMBER" -> "Dec"
                else -> "Jan"
            }
            val dayStr = dt.day.toString().padStart(2, '0')
            val yearStr = dt.year.toString()
            val hourStr = dt.hour.toString().padStart(2, '0')
            val minuteStr = dt.minute.toString().padStart(2, '0')
            val formattedDate = "$monthAbbrev $dayStr, $yearStr $hourStr:$minuteStr"

            MatchDetailsUiState.Success(
                matchId = match.matchId,
                formattedDate = formattedDate,
                blueTeamName = blueName,
                orangeTeamName = orangeName,
                blueTeamLogoUrl = blueTeam?.logoUrl,
                orangeTeamLogoUrl = orangeTeam?.logoUrl,
                blueScore = match.blueScore,
                orangeScore = match.orangeScore,
                blueShots = match.blueShots,
                orangeShots = match.orangeShots,
                blueSaves = match.blueSaves,
                orangeSaves = match.orangeSaves,
                blueAssists = match.blueAssists,
                orangeAssists = match.orangeAssists,
                blueDemos = match.blueDemos,
                orangeDemos = match.orangeDemos
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MatchDetailsUiState.Loading
    )
}
