package com.kingsaul22.etxcenter.feature.calendar

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
import com.kingsaul22.etxcenter.domain.model.CalendarEntry
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCalendarScreen(
    onBackClick: () -> Unit,
    viewModel: ManageCalendarViewModel = koinInject()
) {
    val state by viewModel.uiState.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<CalendarEntry?>(null) }
    var entryToDelete by remember { mutableStateOf<CalendarEntry?>(null) }

    // Forms fields
    var blueTeamId by remember { mutableStateOf("") }
    var orangeTeamId by remember { mutableStateOf("") }
    var dateTimeWindowInput by remember { mutableStateOf("") }
    val selectedMatchIds = remember { mutableStateListOf<String>() }

    // Roster checklist initialization helper
    LaunchedEffect(entryToEdit) {
        selectedMatchIds.clear()
        entryToEdit?.let { entry ->
            blueTeamId = entry.blueTeamId
            orangeTeamId = entry.orangeTeamId
            dateTimeWindowInput = entry.dateTimeWindow
            selectedMatchIds.addAll(entry.matchIds)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Matches Calendar") },
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
                    blueTeamId = ""
                    orangeTeamId = ""
                    dateTimeWindowInput = ""
                    selectedMatchIds.clear()
                    showCreateDialog = true
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Calendar Entry")
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
            } else if (state.calendarEntries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No scheduled matches. Tap + to add one.",
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
                    items(state.calendarEntries, key = { it.id }) { entry ->
                        val blueTeam = state.teams.find { it.id == entry.blueTeamId }
                        val orangeTeam = state.teams.find { it.id == entry.orangeTeamId }
                        val blueName = blueTeam?.name ?: entry.blueTeamId
                        val orangeName = orangeTeam?.name ?: entry.orangeTeamId

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "$blueName vs $orangeName",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Window: ${entry.dateTimeWindow}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                entryToEdit = entry
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Entry",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        IconButton(onClick = { entryToDelete = entry }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Entry",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }

                                // Nested played matches list
                                if (entry.matchIds.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Played Matches:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    entry.matchIds.forEachIndexed { index, matchId ->
                                        val match = state.playedMatches.find { it.matchId == matchId }
                                        val matchText = if (match != null) {
                                            "Game ${index + 1}: Score ${match.blueScore} - ${match.orangeScore}"
                                        } else {
                                            "Game ${index + 1}: Match ID $matchId"
                                        }
                                        Surface(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = MaterialTheme.shapes.extraSmall,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = matchText,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
    }

    // Create Entry Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Calendar Entry") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = dateTimeWindowInput,
                        onValueChange = { dateTimeWindowInput = it },
                        label = { Text("Date & Time Window (e.g. Jun 10, 18:00)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select Blue Team", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        state.teams.forEach { team ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { blueTeamId = team.id }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = blueTeamId == team.id, onClick = { blueTeamId = team.id })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(team.name)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select Orange Team", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        state.teams.forEach { team ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { orangeTeamId = team.id }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = orangeTeamId == team.id, onClick = { orangeTeamId = team.id })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(team.name)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Link Played Matches", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    if (state.playedMatches.isEmpty()) {
                        Text("No played matches available.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            state.playedMatches.forEach { match ->
                                val isSelected = selectedMatchIds.contains(match.matchId)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isSelected) selectedMatchIds.remove(match.matchId)
                                            else selectedMatchIds.add(match.matchId)
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            if (checked == true) selectedMatchIds.add(match.matchId)
                                            else selectedMatchIds.remove(match.matchId)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "ID: ${match.matchId.take(8)}...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Result: ${match.blueScore} - ${match.orangeScore}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        if (blueTeamId.isNotBlank() && orangeTeamId.isNotBlank() && dateTimeWindowInput.isNotBlank()) {
                            viewModel.createCalendarEntry(
                                blueTeamId = blueTeamId,
                                orangeTeamId = orangeTeamId,
                                dateTimeWindow = dateTimeWindowInput,
                                matchIds = selectedMatchIds.toList()
                            )
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

    // Edit Entry Dialog
    entryToEdit?.let { entry ->
        AlertDialog(
            onDismissRequest = { entryToEdit = null },
            title = { Text("Edit Calendar Entry") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = dateTimeWindowInput,
                        onValueChange = { dateTimeWindowInput = it },
                        label = { Text("Date & Time Window (e.g. Jun 10, 18:00)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select Blue Team", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        state.teams.forEach { team ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { blueTeamId = team.id }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = blueTeamId == team.id, onClick = { blueTeamId = team.id })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(team.name)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select Orange Team", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        state.teams.forEach { team ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { orangeTeamId = team.id }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = orangeTeamId == team.id, onClick = { orangeTeamId = team.id })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(team.name)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Link Played Matches", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    if (state.playedMatches.isEmpty()) {
                        Text("No played matches available.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            state.playedMatches.forEach { match ->
                                val isSelected = selectedMatchIds.contains(match.matchId)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isSelected) selectedMatchIds.remove(match.matchId)
                                            else selectedMatchIds.add(match.matchId)
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            if (checked == true) selectedMatchIds.add(match.matchId)
                                            else selectedMatchIds.remove(match.matchId)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "ID: ${match.matchId.take(8)}...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Result: ${match.blueScore} - ${match.orangeScore}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        if (blueTeamId.isNotBlank() && orangeTeamId.isNotBlank() && dateTimeWindowInput.isNotBlank()) {
                            viewModel.updateCalendarEntry(
                                id = entry.id,
                                blueTeamId = blueTeamId,
                                orangeTeamId = orangeTeamId,
                                dateTimeWindow = dateTimeWindowInput,
                                matchIds = selectedMatchIds.toList()
                            )
                            entryToEdit = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Entry Alert Dialog
    entryToDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Delete Entry") },
            text = {
                Text("Are you sure you want to delete this calendar entry? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCalendarEntry(entry.id)
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
