package com.kingsaul22.etxcenter.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun MatchOverviewCard(
    blueTeamName: String,
    orangeTeamName: String,
    modifier: Modifier = Modifier,
    blueTeamLogoUrl: String? = null,
    orangeTeamLogoUrl: String? = null,
    topContent: @Composable (() -> Unit)? = null,
    bottomContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    elevated: Boolean = false
) {
    val content = @Composable {
        Column(modifier = Modifier.padding(16.dp)) {
            if (topContent != null) {
                topContent()
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Blue Team
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    if (blueTeamLogoUrl != null) {
                        TeamLogo(logoUrl = blueTeamLogoUrl, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = blueTeamName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
                
                // VS or Scores
                if (trailingContent != null) {
                    trailingContent()
                } else {
                    Text(
                        text = "VS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Orange Team
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    if (orangeTeamLogoUrl != null) {
                        TeamLogo(logoUrl = orangeTeamLogoUrl, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = orangeTeamName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (bottomContent != null) {
                Spacer(modifier = Modifier.height(12.dp))
                bottomContent()
            }
        }
    }

    if (elevated) {
        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            content = { content() }
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            content = { content() }
        )
    }
}
