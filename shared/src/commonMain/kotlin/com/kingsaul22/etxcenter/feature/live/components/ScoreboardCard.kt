package com.kingsaul22.etxcenter.feature.live.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kingsaul22.etxcenter.core.ui.components.TeamMatchupHeader
import com.kingsaul22.etxcenter.core.ui.localization.LocalStrings

@Composable
fun ScoreboardCard(
    scoreBlue: Int,
    scoreOrange: Int
) {
    val strings = LocalStrings.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TeamMatchupHeader(
                blueTeamName = strings.blueShort,
                orangeTeamName = strings.orangeShort,
                blueTeamLogoUrl = null,
                orangeTeamLogoUrl = null,
                centerContent = {
                    Text(
                        text = "$scoreBlue - $scoreOrange",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    }
}
