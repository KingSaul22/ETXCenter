package com.kingsaul22.etxcenter.feature.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kingsaul22.etxcenter.core.ui.components.TeamLogo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailsScreen(
    viewModel: MatchDetailsViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(screenPadding)
        ) {
            when (val uiState = state) {
                is MatchDetailsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is MatchDetailsUiState.NotFound -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Match not found.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = onBackClick) {
                            Text("Go Back")
                        }
                    }
                }
                is MatchDetailsUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Match Header Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = uiState.formattedDate,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Blue Team
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        TeamLogo(
                                            logoUrl = uiState.blueTeamLogoUrl,
                                            contentDescription = uiState.blueTeamName,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = uiState.blueTeamName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2
                                        )
                                    }

                                    // Score
                                    Text(
                                        text = "${uiState.blueScore} - ${uiState.orangeScore}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )

                                    // Orange Team
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        TeamLogo(
                                            logoUrl = uiState.orangeTeamLogoUrl,
                                            contentDescription = uiState.orangeTeamName,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = uiState.orangeTeamName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }

                        // Comparative Stats Section
                        Text(
                            text = "Comparative Stats",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                StatProgressBarRow(
                                    label = "Shots",
                                    blueValue = uiState.blueShots,
                                    orangeValue = uiState.orangeShots
                                )
                                StatProgressBarRow(
                                    label = "Assists",
                                    blueValue = uiState.blueAssists,
                                    orangeValue = uiState.orangeAssists
                                )
                                StatProgressBarRow(
                                    label = "Saves",
                                    blueValue = uiState.blueSaves,
                                    orangeValue = uiState.orangeSaves
                                )
                                StatProgressBarRow(
                                    label = "Demos",
                                    blueValue = uiState.blueDemos,
                                    orangeValue = uiState.orangeDemos
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatProgressBarRow(
    label: String,
    blueValue: Long,
    orangeValue: Long
) {
    val isBlueHigher = blueValue > orangeValue
    val isOrangeHigher = orangeValue > blueValue

    // Calculate weights handling the 0-0 edge case
    val blueWeight = if (blueValue == 0L && orangeValue == 0L) 1f else blueValue.toFloat()
    val orangeWeight = if (blueValue == 0L && orangeValue == 0L) 1f else orangeValue.toFloat()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Label Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = blueValue.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isBlueHigher) FontWeight.Bold else FontWeight.Normal,
                color = if (isBlueHigher) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = orangeValue.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isOrangeHigher) FontWeight.Bold else FontWeight.Normal,
                color = if (isOrangeHigher) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // Stacked Progress Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (blueWeight > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(blueWeight)
                        .background(Color(0xFF1565C0))
                )
            }
            if (blueWeight > 0f && orangeWeight > 0f) {
                Spacer(modifier = Modifier.width(2.dp))
            }
            if (orangeWeight > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(orangeWeight)
                        .background(Color(0xFFE65100))
                )
            }
        }
    }
}
