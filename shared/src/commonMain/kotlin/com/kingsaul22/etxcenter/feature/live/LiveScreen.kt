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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kingsaul22.etxcenter.domain.model.LiveEvent
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

private val BlueTeam = Color(0xFF1565C0)
private val OrangeTeam = Color(0xFFE65100)
private val ClockWarning = Color(0xFFFFA000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen(
    viewModel: LiveViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Match") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
                Box(
                    modifier = Modifier.fillMaxSize().padding(screenPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active match running",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

                    Text(
                        text = "Live Events",
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

@Composable
private fun MatchHeader(
    arena: String,
    isOvertime: Boolean,
    hasWinner: Boolean,
    winner: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = arena,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (isOvertime) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = ClockWarning,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "OT",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        if (hasWinner && winner.isNotEmpty()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${winner.uppercase()} WINS",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (winner == "blue") BlueTeam else OrangeTeam
            )
        }
    }
}

@Composable
private fun EventsEmptyPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "No events yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Goals, saves, and other events will appear here.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun LiveEventCard(event: LiveEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.displayText(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = event.formattedTime(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ScoreboardCard(
    scoreBlue: Int,
    scoreOrange: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamScore(
                label = "BLUE",
                score = scoreBlue,
                color = BlueTeam,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "VS",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline
            )

            TeamScore(
                label = "ORANGE",
                score = scoreOrange,
                color = OrangeTeam,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TeamScore(
    label: String,
    score: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            color = color,
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = label.first().toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = color
        )

        Text(
            text = score.toString(),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ClockRow(
    timeRemainingSeconds: Long,
    hasWinner: Boolean
) {
    val mm = (timeRemainingSeconds / 60).toString().padStart(2, '0')
    val ss = (timeRemainingSeconds % 60).toString().padStart(2, '0')
    val formatted = "$mm:$ss"
    val timeColor = when {
        hasWinner -> MaterialTheme.colorScheme.onSurfaceVariant
        timeRemainingSeconds < 30 -> ClockWarning
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (hasWinner) "FINAL" else formatted,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Medium,
            color = timeColor
        )
    }
}

private fun LiveEvent.displayText(): String = when (type) {
    "countdownbegin" -> "Countdown started"
    "ballhit" -> {
        val preSpeed = data["pre_hit_speed"] ?: "0"
        val postSpeed = data["post_hit_speed"] ?: "0"
        "Ball hit \u2014 speed: $preSpeed \u2192 $postSpeed kph"
    }
    else -> type
}

private fun LiveEvent.formattedTime(): String {
    val dt = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    val hh = dt.hour.toString().padStart(2, '0')
    val mm = dt.minute.toString().padStart(2, '0')
    val ss = dt.second.toString().padStart(2, '0')
    return "$hh:$mm:$ss"
}
