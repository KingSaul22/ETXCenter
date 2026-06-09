package com.kingsaul22.etxcenter.feature.players

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kingsaul22.etxcenter.domain.model.Player
import org.koin.compose.koinInject

import com.kingsaul22.etxcenter.core.ui.localization.LocalStrings
import com.kingsaul22.etxcenter.core.ui.components.EtxTopAppBar
import com.kingsaul22.etxcenter.core.ui.components.StandardConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePlayersScreen(
    onBackClick: () -> Unit,
    viewModel: ManagePlayersViewModel = koinInject()
) {
    val state by viewModel.uiState.collectAsState()
    val strings = LocalStrings.current

    var showCreateDialog by remember { mutableStateOf(false) }
    var playerToEdit by remember { mutableStateOf<Player?>(null) }
    var playerToDelete by remember { mutableStateOf<Player?>(null) }



    Scaffold(
        topBar = {
            EtxTopAppBar(
                title = strings.managePlayersTitle,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = strings.addPlayerDesc)
            }
        }
    ) { screenPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(screenPadding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.players.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.emptyPlayersMsg,
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
                    items(state.players, key = { it.id }) { player ->
                        val team = state.teams.find { it.id == player.teamId }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = player.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "ID: ${player.id}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (team != null) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = MaterialTheme.shapes.extraSmall
                                        ) {
                                            Text(
                                                text = team.name,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else {
                                        Surface(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = MaterialTheme.shapes.extraSmall
                                        ) {
                                            Text(
                                                text = strings.unassignedMsg,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { playerToEdit = player }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = strings.editRoster,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = { playerToDelete = player }) {
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

    // Add Player Dialog
    if (showCreateDialog) {
        PlayerFormDialog(
            title = strings.createPlayerTitle,
            initialPlayer = null,
            teams = state.teams,
            onConfirm = { id, name, teamId ->
                viewModel.createPlayer(id, name, teamId)
                showCreateDialog = false
            },
            confirmLabel = strings.create,
            onDismiss = { showCreateDialog = false }
        )
    }

    // Edit Player Dialog
    playerToEdit?.let { player ->
        PlayerFormDialog(
            title = strings.editPlayerTitle,
            initialPlayer = player,
            teams = state.teams,
            onConfirm = { id, name, teamId ->
                viewModel.updatePlayer(id, name, teamId)
                playerToEdit = null
            },
            confirmLabel = strings.save,
            onDismiss = { playerToEdit = null }
        )
    }

    // Delete Player Confirmation Dialog
    playerToDelete?.let { player ->
        val confirmMsg = strings.deletePlayerConfirmMsgDynamic.replace("{name}", player.displayName)
        StandardConfirmDialog(
            title = strings.deletePlayerConfirmTitle,
            message = confirmMsg,
            confirmText = strings.delete,
            dismissText = strings.cancel,
            isDestructive = true,
            onConfirm = {
                viewModel.deletePlayer(player.id)
                playerToDelete = null
            },
            onDismiss = { playerToDelete = null }
        )
    }
}

@Composable
private fun PlayerFormDialog(
    title: String,
    initialPlayer: Player?,
    teams: List<com.kingsaul22.etxcenter.domain.model.Team>,
    onConfirm: (id: String, name: String, teamId: String?) -> Unit,
    confirmLabel: String,
    onDismiss: () -> Unit
) {
    val strings = LocalStrings.current
    var playerIdInput by remember(initialPlayer) { mutableStateOf(initialPlayer?.id ?: "") }
    var displayNameInput by remember(initialPlayer) { mutableStateOf(initialPlayer?.displayName ?: "") }
    var selectedTeamId by remember(initialPlayer) { mutableStateOf(initialPlayer?.teamId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = playerIdInput,
                    onValueChange = { if (initialPlayer == null) playerIdInput = it },
                    label = { Text(if (initialPlayer == null) strings.platformIdLabel else strings.playerIdReadOnlyLabel) },
                    singleLine = true,
                    enabled = initialPlayer == null,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = displayNameInput,
                    onValueChange = { displayNameInput = it },
                    label = { Text(strings.playerNameLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(strings.selectTeamLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTeamId = null }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedTeamId == null, onClick = { selectedTeamId = null })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.unassignedMsg)
                    }
                    teams.forEach { team ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTeamId = team.id }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedTeamId == team.id, onClick = { selectedTeamId = team.id })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(team.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (playerIdInput.isNotBlank() && displayNameInput.isNotBlank()) {
                        onConfirm(playerIdInput, displayNameInput, selectedTeamId)
                    }
                }
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}
