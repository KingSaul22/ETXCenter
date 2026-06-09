package com.kingsaul22.etxcenter.feature.live.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kingsaul22.etxcenter.core.ui.localization.LocalStrings

@Composable
fun MatchHeader(
    arena: String,
    isOvertime: Boolean,
    hasWinner: Boolean,
    winner: String
) {
    val strings = LocalStrings.current

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
                    text = strings.otBadge,
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
                text = "${winner.uppercase()} ${strings.winsSuffix}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (winner == "blue") BlueTeam else OrangeTeam
            )
        }
    }
}
