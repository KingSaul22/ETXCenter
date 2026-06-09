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

import com.kingsaul22.etxcenter.core.ui.localization.LocalStrings
import com.kingsaul22.etxcenter.core.ui.components.EtxTopAppBar
import com.kingsaul22.etxcenter.core.ui.components.StandardConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTeamsScreen(
    onBackClick: () -> Unit,
    viewModel: ManageTeamsViewModel = koinInject()
) {
    val teams by viewModel.teams.collectAsState()
    val allPlayers by viewModel.players.collectAsState()
    val strings = LocalStrings.current

    var showCreateDialog by remember { mutableStateOf(false) }
    var teamToDelete by remember { mutableStateOf<Team?>(null) }
    var teamRosterToEdit by remember { mutableStateOf<Team?>(null) }

    Scaffold(
        topBar = {
            EtxTopAppBar(
                title = strings.manageTeamsTitle,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = strings.addTeamDesc)
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
                        text = strings.emptyTeamsMsg,
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
                    items(teams, key = { it.id }) { team ->
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
                                        Text(strings.editRoster)
                                    }
                                    IconButton(onClick = { teamToDelete = team }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = strings.delete,
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
        CreateTeamDialog(
            onConfirm = { name ->
                viewModel.createTeam(name)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    teamToDelete?.let { team ->
        val confirmMsg = strings.deleteTeamConfirmMsgDynamic.replace("{name}", team.name)
        StandardConfirmDialog(
            title = strings.deleteTeamConfirmTitle,
            message = confirmMsg,
            confirmText = strings.delete,
            dismissText = strings.cancel,
            isDestructive = true,
            onConfirm = {
                viewModel.deleteTeam(team.id)
                teamToDelete = null
            },
            onDismiss = { teamToDelete = null }
        )
    }

    teamRosterToEdit?.let { team ->
        TeamRosterDialog(
            team = team,
            allPlayers = allPlayers,
            onConfirm = { playerIds ->
                viewModel.updateTeamRoster(team.id, playerIds)
                teamRosterToEdit = null
            },
            onDismiss = { teamRosterToEdit = null }
        )
    }
}

@Composable
private fun CreateTeamDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalStrings.current
    var teamNameInput by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.createNewTeam) },
        text = {
            OutlinedTextField(
                value = teamNameInput,
                onValueChange = { teamNameInput = it },
                label = { Text(strings.teamNameLabel) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (teamNameInput.isNotBlank() && !isSubmitting) {
                        isSubmitting = true
                        onConfirm(teamNameInput)
                    }
                },
                enabled = !isSubmitting
            ) {
                Text(strings.create)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}

@Composable
private fun TeamRosterDialog(
    team: Team,
    allPlayers: List<com.kingsaul22.etxcenter.domain.model.Player>,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalStrings.current
    var isSubmitting by remember { mutableStateOf(false) }
    val selectedPlayerIds = remember(team) {
        mutableStateListOf<String>().apply {
            addAll(allPlayers.filter { it.teamId == team.id }.map { it.id })
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${strings.editRosterTitle}: ${team.name}") },
        text = {
            if (allPlayers.isEmpty()) {
                Text(strings.noPlayersAvailableMsg)
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
                                        text = "${strings.currentlyOnMsg}${player.teamId}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                } else if (player.teamId == team.id) {
                                    Text(
                                        text = strings.assignedToTeamMsg,
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
                    if (!isSubmitting) {
                        isSubmitting = true
                        onConfirm(selectedPlayerIds.toList())
                    }
                },
                enabled = !isSubmitting
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}
