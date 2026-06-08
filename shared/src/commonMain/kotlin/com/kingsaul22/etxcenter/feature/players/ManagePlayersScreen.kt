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

    // Forms fields
    var playerIdInput by remember { mutableStateOf("") }
    var displayNameInput by remember { mutableStateOf("") }
    var selectedTeamId by remember { mutableStateOf<String?>(null) }
    
    val addPlayerDesc = if (strings.languageCode == "es") "Añadir Jugador" else "Add Player"
    val emptyPlayersMsg = if (strings.languageCode == "es") "No hay jugadores disponibles. Toca + para agregar uno." else "No players available. Tap + to add one."
    val unassignedMsg = if (strings.languageCode == "es") "Sin asignar / Sin equipo" else "Unassigned / No Team"
    val createPlayerTitle = if (strings.languageCode == "es") "Crear Nuevo Jugador" else "Create New Player"
    val platformIdLabel = if (strings.languageCode == "es") "ID de Plataforma (ej. Epic|hash|0)" else "Platform ID (e.g. Epic|hash|0)"
    val selectTeamLabel = if (strings.languageCode == "es") "Seleccionar Asignación de Equipo" else "Select Team Assignment"
    val editPlayerTitle = if (strings.languageCode == "es") "Editar Jugador" else "Edit Player"
    val playerIdReadOnly = if (strings.languageCode == "es") "ID del Jugador (Solo lectura)" else "Player ID (Read-only)"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.managePlayersTitle) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.goBack
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
                    playerIdInput = ""
                    displayNameInput = ""
                    selectedTeamId = null
                    showCreateDialog = true
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = addPlayerDesc)
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
                        text = emptyPlayersMsg,
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
                                                text = unassignedMsg,
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
                                        onClick = {
                                            playerToEdit = player
                                            displayNameInput = player.displayName
                                            selectedTeamId = player.teamId
                                        }
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
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(createPlayerTitle) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = playerIdInput,
                        onValueChange = { playerIdInput = it },
                        label = { Text(platformIdLabel) },
                        singleLine = true,
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
                    Text(selectTeamLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

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
                            Text(unassignedMsg)
                        }
                        state.teams.forEach { team ->
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
                            viewModel.createPlayer(playerIdInput, displayNameInput, selectedTeamId)
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text(strings.create)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // Edit Player Dialog
    playerToEdit?.let { player ->
        AlertDialog(
            onDismissRequest = { playerToEdit = null },
            title = { Text(editPlayerTitle) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = player.id,
                        onValueChange = {},
                        label = { Text(playerIdReadOnly) },
                        enabled = false,
                        singleLine = true,
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
                    Text(selectTeamLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

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
                            Text(unassignedMsg)
                        }
                        state.teams.forEach { team ->
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
                        if (displayNameInput.isNotBlank()) {
                            viewModel.updatePlayer(player.id, displayNameInput, selectedTeamId)
                            playerToEdit = null
                        }
                    }
                ) {
                    Text(strings.save)
                }
            },
            dismissButton = {
                TextButton(onClick = { playerToEdit = null }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // Delete Player Confirmation Dialog
    playerToDelete?.let { player ->
        val confirmMsg = if (strings.languageCode == "es") "¿Estás seguro de que deseas eliminar al jugador \"${player.displayName}\"? Esto lo desasignará de su equipo y eliminará sus estadísticas acumuladas. Esta acción no se puede deshacer." else "Are you sure you want to delete player \"${player.displayName}\"? This will unassign them from their team roster and delete their cumulative stats. This action cannot be undone."
        AlertDialog(
            onDismissRequest = { playerToDelete = null },
            title = { Text(strings.deletePlayerConfirmTitle) },
            text = {
                Text(confirmMsg)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePlayer(player.id)
                        playerToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(strings.delete)
                }
            },
            dismissButton = {
                TextButton(onClick = { playerToDelete = null }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}
