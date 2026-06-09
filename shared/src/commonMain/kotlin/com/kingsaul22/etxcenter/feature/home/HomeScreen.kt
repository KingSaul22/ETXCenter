package com.kingsaul22.etxcenter.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kingsaul22.etxcenter.core.ui.components.QuickActionCard
import com.kingsaul22.etxcenter.core.ui.components.StatCard
import com.kingsaul22.etxcenter.core.ui.components.TeamLogo
import com.kingsaul22.etxcenter.core.ui.localization.Language
import com.kingsaul22.etxcenter.core.ui.localization.LocalLanguage
import com.kingsaul22.etxcenter.core.ui.localization.LocalLanguageController
import com.kingsaul22.etxcenter.core.ui.localization.LocalStrings
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import com.kingsaul22.etxcenter.core.ui.components.EtxTopAppBar
import com.kingsaul22.etxcenter.core.ui.components.MatchOverviewCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAdminClick: () -> Unit,
    onManageTeamsClick: () -> Unit,
    onManagePlayersClick: () -> Unit,
    onManageMetadataClick: () -> Unit,
    onManageCalendarClick: () -> Unit,
    viewModel: HomeViewModel = koinInject()
) {
    val state by viewModel.uiState.collectAsState()
    val strings = LocalStrings.current
    val currentLanguage = LocalLanguage.current
    val languageController = LocalLanguageController.current

    Scaffold(
        topBar = {
            EtxTopAppBar(
                title = strings.appTitle,
                actions = {
                    // Language Switcher Toggle Button
                    IconButton(
                        onClick = {
                            val nextLang = if (currentLanguage == Language.EN) Language.ES else Language.EN
                            languageController(nextLang)
                        }
                    ) {
                        Text(
                            text = if (currentLanguage == Language.EN) "ES" else "EN",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    IconButton(
                        onClick = {
                            if (state.isAdmin) {
                                viewModel.signOutAdmin()
                            } else {
                                onAdminClick()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (state.isAdmin) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = if (state.isAdmin) strings.adminLogout else strings.adminLogin
                        )
                    }
                }
            )
        }
    ) { screenPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(screenPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Prominent Admin Banner
            if (state.isAdmin) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.adminModeActive,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            if (state.isAdmin) {
                AdminHomeContent(
                    state = state,
                    onManageTeamsClick = onManageTeamsClick,
                    onManagePlayersClick = onManagePlayersClick,
                    onManageMetadataClick = onManageMetadataClick,
                    onManageCalendarClick = onManageCalendarClick
                )
            } else {
                FanHomeContent(state = state)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun formatEpochToDateTime(epochSeconds: Long): String {
    return try {
        val instant = Instant.fromEpochSeconds(epochSeconds)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val year = localDateTime.year
        val month = localDateTime.month.toString().padStart(2, '0')
        val day = localDateTime.day.toString().padStart(2, '0')
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        "$year-$month-$day $hour:$minute"
    } catch (e: Exception) {
        ""
    }
}

@Composable
private fun AdminHomeContent(
    state: HomeUiState,
    onManageTeamsClick: () -> Unit,
    onManagePlayersClick: () -> Unit,
    onManageMetadataClick: () -> Unit,
    onManageCalendarClick: () -> Unit
) {
    val strings = LocalStrings.current

    Text(
        text = strings.welcomeBackAdmin,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = strings.dbStatusTitle,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = strings.statTeams,
            value = state.teamCount.toString(),
            iconLetter = "T",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = strings.statPlayers,
            value = state.playerCount.toString(),
            iconLetter = "P",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = strings.statEvents,
            value = state.activeEventsCount.toString(),
            iconLetter = "E",
            modifier = Modifier.weight(1f)
        )
    }

    Text(
        text = strings.quickActions,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            label = strings.actionManageTeams,
            onClick = onManageTeamsClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            label = strings.actionManagePlayers,
            onClick = onManagePlayersClick,
            modifier = Modifier.weight(1f)
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            label = strings.actionEditMetadata,
            onClick = onManageMetadataClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            label = strings.actionManageCalendar,
            onClick = onManageCalendarClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FanHomeContent(
    state: HomeUiState
) {
    val strings = LocalStrings.current

    Text(
        text = strings.welcomeToEtx,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = strings.trackFavoriteTeams,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ETX League Hub",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SAVETX connects Rocket League players, statistics, and live updates directly to the cloud. Explore teams, match profiles, and follow live feeds from the dashboard.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }

    Text(
        text = strings.competitionResume,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = "Total ${strings.statTeams}",
            value = state.teamCount.toString(),
            iconLetter = "T",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Total ${strings.statPlayers}",
            value = state.playerCount.toString(),
            iconLetter = "P",
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = strings.upcomingMatchesTitle,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )

    if (state.upcomingMatches.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = strings.noUpcomingMatches,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        state.upcomingMatches.forEach { entry ->
            MatchOverviewCard(
                blueTeamName = entry.blueTeamName,
                orangeTeamName = entry.orangeTeamName,
                blueTeamLogoUrl = entry.blueTeamLogoUrl,
                orangeTeamLogoUrl = entry.orangeTeamLogoUrl,
                topContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statusColor = if (entry.status == HomeMatchStatus.ACTIVE) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                        val statusText = if (entry.status == HomeMatchStatus.ACTIVE) {
                            strings.statusInProgress
                        } else {
                            strings.statusScheduled
                        }
                        Surface(
                            color = statusColor.copy(alpha = 0.15f),
                            contentColor = statusColor,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = statusText.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (entry.status == HomeMatchStatus.ACTIVE && entry.gamesPlayed > 0) {
                            Text(
                                text = "${strings.gamesPlayedLabel}: ${entry.gamesPlayed}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                bottomContent = {
                    val startStr = formatEpochToDateTime(entry.startTimeEpoch)
                    val endStr = formatEpochToDateTime(entry.endTimeEpoch)
                    Text(
                        text = "$startStr - $endStr",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}