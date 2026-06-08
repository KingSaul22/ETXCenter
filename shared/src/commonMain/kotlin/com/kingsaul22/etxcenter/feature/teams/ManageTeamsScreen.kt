package com.kingsaul22.etxcenter.feature.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val allPlayers by viewModel.players.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var teamNameInput by remember { mutableStateOf("") }
    var teamToDelete by remember { mutableStateOf<Team?>(null) }
    var teamRosterToEdit by remember { mutableStateOf<Team?>(null) }

    val selectedPlayerIds = remember { mutableStateListOf<String>() }

    LaunchedEffect(teamRosterToEdit) {
        selectedPlayerIds.clear()
        val currentTeamId = teamRosterToEdit?.id ?: return@LaunchedEffect
        val rosterPlayers = allPlayers.filter { it.teamId == currentTeamId }.map { it.id }
        selectedPlayerIds.addAll(rosterPlayers)
    }

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
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TextButton(onClick = { teamRosterToEdit = team }) {
                                        Text("Edit Roster")
                                    }
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

    teamRosterToEdit?.let { team ->
        AlertDialog(
            onDismissRequest = { teamRosterToEdit = null },
            title = { Text("Edit Roster: ${team.name}") },
            text = {
                if (allPlayers.isEmpty()) {
                    Text("No players available to assign.")
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allPlayers.forEach { player ->
                            val isSelected = selectedPlayerIds.contains(player.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isSelected) {
                                            selectedPlayerIds.remove(player.id)
                                        } else {
                                            selectedPlayerIds.add(player.id)
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        if (checked == true) {
                                            selectedPlayerIds.add(player.id)
                                        } else {
                                            selectedPlayerIds.remove(player.id)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = player.displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (player.teamId != null && player.teamId != team.id) {
                                        Text(
                                            text = "Currently on: ${player.teamId}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    } else if (player.teamId == team.id) {
                                        Text(
                                            text = "Assigned to this team",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateTeamRoster(team.id, selectedPlayerIds.toList())
                        teamRosterToEdit = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { teamRosterToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
