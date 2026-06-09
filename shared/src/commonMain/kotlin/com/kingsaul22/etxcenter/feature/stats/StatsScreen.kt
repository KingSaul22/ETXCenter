package com.kingsaul22.etxcenter.feature.stats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

import org.koin.compose.koinInject

import com.kingsaul22.etxcenter.core.ui.localization.LocalStrings
import com.kingsaul22.etxcenter.core.ui.components.EtxTopAppBar
import com.kingsaul22.etxcenter.core.ui.components.MatchOverviewCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onMatchClick: (String) -> Unit,
    viewModel: StatsViewModel = koinInject()
) {
    val state by viewModel.uiState.collectAsState()
    val strings = LocalStrings.current

    Scaffold(
        topBar = {
            EtxTopAppBar(
                title = strings.matchHistoryTitle
            )
        }
    ) { screenPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(screenPadding)
        ) {
            when (val uiState = state) {
                is StatsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is StatsUiState.Success -> {
                    if (uiState.matches.isEmpty()) {
                        Text(
                            text = strings.noMatchesRecorded,
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.matches, key = { it.matchId }) { match ->
                                MatchCard(
                                    match = match,
                                    onClick = { onMatchClick(match.matchId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchCard(
    match: MatchProfile,
    onClick: () -> Unit
) {
    val isBlueWinner = match.blueScore > match.orangeScore
    val isOrangeWinner = match.orangeScore > match.blueScore

    MatchOverviewCard(
        blueTeamName = match.blueTeamName,
        orangeTeamName = match.orangeTeamName,
        modifier = Modifier.clickable { onClick() },
        elevated = true,
        topContent = {
            Text(
                text = match.formattedDate,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = match.blueScore.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = if (isBlueWinner) FontWeight.Bold else FontWeight.Normal,
                    color = if (isBlueWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = " - ",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = match.orangeScore.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = if (isOrangeWinner) FontWeight.Bold else FontWeight.Normal,
                    color = if (isOrangeWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
