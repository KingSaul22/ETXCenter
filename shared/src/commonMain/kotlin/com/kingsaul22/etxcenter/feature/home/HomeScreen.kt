package com.kingsaul22.etxcenter.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.kingsaul22.etxcenter.core.ui.localization.Language
import com.kingsaul22.etxcenter.core.ui.localization.LocalLanguage
import com.kingsaul22.etxcenter.core.ui.localization.LocalLanguageController
import com.kingsaul22.etxcenter.core.ui.localization.LocalStrings
import org.koin.compose.koinInject

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
            TopAppBar(
                title = { Text(strings.appTitle) },
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
                // Admin specific layout
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
            } else {
                // Fan/Non-Admin layout
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
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}