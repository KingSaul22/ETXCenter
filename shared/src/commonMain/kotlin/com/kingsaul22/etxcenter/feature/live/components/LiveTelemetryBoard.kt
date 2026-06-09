package com.kingsaul22.etxcenter.feature.live.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.kingsaul22.etxcenter.core.ui.localization.LocalStrings
import com.kingsaul22.etxcenter.domain.model.PlayerTelemetry

@Composable
fun LiveTelemetryBoard(telemetry: List<PlayerTelemetry>) {
    val strings = LocalStrings.current
    val bluePlayers = telemetry.filter { it.team == 0 }
    val orangePlayers = telemetry.filter { it.team == 1 }

    Text(
        text = strings.playerTelemetry,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )

    if (bluePlayers.isNotEmpty()) {
        TeamTelemetrySection(
            label = strings.blueTeam,
            color = BlueTeam,
            players = bluePlayers
        )
    }

    if (orangePlayers.isNotEmpty()) {
        TeamTelemetrySection(
            label = strings.orangeTeam,
            color = OrangeTeam,
            players = orangePlayers
        )
    }
}

@Composable
private fun TeamTelemetrySection(
    label: String,
    color: Color,
    players: List<PlayerTelemetry>
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = color
    )

    players.forEach { player ->
        PlayerTelemetryCard(player = player, boostColor = color)
    }
}
