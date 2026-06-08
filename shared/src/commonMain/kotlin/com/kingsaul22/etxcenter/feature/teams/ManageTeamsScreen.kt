package com.kingsaul22.etxcenter.feature.teams

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kingsaul22.etxcenter.domain.model.Team
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTeamsScreen(
    onBackClick: () -> Unit,
    viewModel: ManageTeamsViewModel = koinInject()
) {
    val teams by viewModel.teams.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var teamNameInput by remember { mutableStateOf("") }
    var teamToDelete by remember { mutableStateOf<Team?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Teams") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    teamNameInput = ""
                    showCreateDialog = true
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Team")
            }
        }
    ) { screenPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(screenPadding)
        ) {
            if (teams.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No teams available. Tap + to add one.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(teams) { team ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = team.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                IconButton(onClick = { teamToDelete = team }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Team",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create New Team") },
            text = {
                OutlinedTextField(
                    value = teamNameInput,
                    onValueChange = { teamNameInput = it },
                    label = { Text("Team Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (teamNameInput.isNotBlank()) {
                            viewModel.createTeam(teamNameInput)
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    teamToDelete?.let { team ->
        AlertDialog(
            onDismissRequest = { teamToDelete = null },
            title = { Text("Delete Team") },
            text = {
                Text("Are you sure you want to delete team \"${team.name}\"? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTeam(team.id)
                        teamToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { teamToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
