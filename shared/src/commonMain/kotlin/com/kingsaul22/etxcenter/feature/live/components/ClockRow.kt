package com.kingsaul22.etxcenter.feature.live.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kingsaul22.etxcenter.core.ui.localization.LocalStrings

@Composable
fun ClockRow(
    timeRemainingSeconds: Long,
    hasWinner: Boolean
) {
    val strings = LocalStrings.current
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
            text = if (hasWinner) strings.finalClock else formatted,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Medium,
            color = timeColor
        )
    }
}
