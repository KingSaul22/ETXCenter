package com.kingsaul22.etxcenter.feature.live

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kingsaul22.etxcenter.core.ui.localization.AppStrings
import com.kingsaul22.etxcenter.core.ui.localization.LocalStrings
import com.kingsaul22.etxcenter.core.ui.components.EtxTopAppBar
import com.kingsaul22.etxcenter.core.ui.components.TeamMatchupHeader
import com.kingsaul22.etxcenter.domain.model.LiveEvent
import com.kingsaul22.etxcenter.domain.model.PlayerTelemetry
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

import com.kingsaul22.etxcenter.feature.live.components.ClockRow
import com.kingsaul22.etxcenter.feature.live.components.EventsEmptyPlaceholder
import com.kingsaul22.etxcenter.feature.live.components.LiveEventCard
import com.kingsaul22.etxcenter.feature.live.components.LiveTelemetryBoard
import com.kingsaul22.etxcenter.feature.live.components.MatchHeader
import com.kingsaul22.etxcenter.feature.live.components.ScoreboardCard
import com.kingsaul22.etxcenter.feature.live.components.TwitchStreamCard

private const val TwitchChannel = "etxclan"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen(
    viewModel: LiveViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalStrings.current
    var isStreamVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            EtxTopAppBar(
                title = strings.liveMatchTitle
            )
        }
    ) { screenPadding ->
        when (val state = uiState) {
            LiveUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(screenPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            LiveUiState.NoActiveMatch -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(screenPadding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    TwitchStreamCard(
                        channel = TwitchChannel,
                        isExpanded = isStreamVisible,
                        onToggleExpanded = { isStreamVisible = !isStreamVisible }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.noActiveMatch,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is LiveUiState.ActiveMatch -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(screenPadding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    TwitchStreamCard(
                        channel = TwitchChannel,
                        isExpanded = isStreamVisible,
                        onToggleExpanded = { isStreamVisible = !isStreamVisible }
                    )

                    MatchHeader(
                        arena = state.arena,
                        isOvertime = state.isOvertime,
                        hasWinner = state.hasWinner,
                        winner = state.winner
                    )

                    ScoreboardCard(
                        scoreBlue = state.scoreBlue,
                        scoreOrange = state.scoreOrange
                    )

                    ClockRow(
                        timeRemainingSeconds = state.timeRemainingSeconds,
                        hasWinner = state.hasWinner
                    )

                    if (state.playerTelemetry.isNotEmpty()) {
                        LiveTelemetryBoard(telemetry = state.playerTelemetry)
                    }

                    Text(
                        text = strings.liveEvents,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (state.events.isEmpty()) {
                        EventsEmptyPlaceholder()
                    } else {
                        state.events.forEach { event ->
                            LiveEventCard(event = event)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

