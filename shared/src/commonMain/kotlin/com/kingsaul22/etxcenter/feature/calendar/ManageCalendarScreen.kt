package com.kingsaul22.etxcenter.feature.calendar

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
import com.kingsaul22.etxcenter.core.ui.components.DateTimePickerField
import com.kingsaul22.etxcenter.core.ui.components.SearchableTeamDropdown
import com.kingsaul22.etxcenter.domain.model.CalendarEntry
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
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

    // Form fields — date and time are split for picker integration
    var blueTeamId by remember { mutableStateOf("") }
    var orangeTeamId by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf<String?>(null) }

    // Form initialization helper — populate fields when editing
    LaunchedEffect(entryToEdit) {
        showError = null
        entryToEdit?.let { entry ->
            blueTeamId = entry.blueTeamId
            orangeTeamId = entry.orangeTeamId
            val (sd, st) = splitEpochToDateAndTime(entry.startTime.epochSeconds)
            val (ed, et) = splitEpochToDateAndTime(entry.endTime.epochSeconds)
            startDate = sd
            startTime = st
            endDate = ed
            endTime = et
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
                    startDate = ""
                    startTime = ""
                    endDate = ""
                    endTime = ""
                    showError = null
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

                        val startStr = formatEpochToDateTime(entry.startTime.epochSeconds)
                        val endStr = formatEpochToDateTime(entry.endTime.epochSeconds)

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
                                            text = "Window: $startStr - $endStr",
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

                                // Dynamic list of matched games
                                if (entry.matchIds.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Played Matches (Auto-Joined):",
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

    // =============================================
    // Create Entry Dialog
    // =============================================
    if (showCreateDialog) {
        CalendarEntryDialog(
            title = "Create Calendar Entry",
            startDate = startDate,
            startTime = startTime,
            endDate = endDate,
            endTime = endTime,
            onStartDateChange = { startDate = it },
            onStartTimeChange = { startTime = it },
            onEndDateChange = { endDate = it },
            onEndTimeChange = { endTime = it },
            blueTeamId = blueTeamId,
            orangeTeamId = orangeTeamId,
            onBlueTeamSelected = { blueTeamId = it },
            onOrangeTeamSelected = { orangeTeamId = it },
            teams = state.teams,
            errorMessage = showError,
            onConfirm = {
                val start = parseDateTimeToEpoch("$startDate $startTime")
                val end = parseDateTimeToEpoch("$endDate $endTime")
                if (start == null || end == null) {
                    showError = "Invalid Date-Time. Use YYYY-MM-DD for dates and HH:mm for times."
                    return@CalendarEntryDialog
                }
                if (blueTeamId.isBlank() || orangeTeamId.isBlank()) {
                    showError = "Please select both teams"
                    return@CalendarEntryDialog
                }
                if (start > end) {
                    showError = "Start time must be before End time"
                    return@CalendarEntryDialog
                }
                viewModel.createCalendarEntry(
                    blueTeamId = blueTeamId,
                    orangeTeamId = orangeTeamId,
                    startTime = start,
                    endTime = end
                )
                showCreateDialog = false
            },
            confirmLabel = "Create",
            onDismiss = { showCreateDialog = false }
        )
    }

    // =============================================
    // Edit Entry Dialog
    // =============================================
    entryToEdit?.let { entry ->
        CalendarEntryDialog(
            title = "Edit Calendar Entry",
            startDate = startDate,
            startTime = startTime,
            endDate = endDate,
            endTime = endTime,
            onStartDateChange = { startDate = it },
            onStartTimeChange = { startTime = it },
            onEndDateChange = { endDate = it },
            onEndTimeChange = { endTime = it },
            blueTeamId = blueTeamId,
            orangeTeamId = orangeTeamId,
            onBlueTeamSelected = { blueTeamId = it },
            onOrangeTeamSelected = { orangeTeamId = it },
            teams = state.teams,
            errorMessage = showError,
            onConfirm = {
                val start = parseDateTimeToEpoch("$startDate $startTime")
                val end = parseDateTimeToEpoch("$endDate $endTime")
                if (start == null || end == null) {
                    showError = "Invalid Date-Time. Use YYYY-MM-DD for dates and HH:mm for times."
                    return@CalendarEntryDialog
                }
                if (blueTeamId.isBlank() || orangeTeamId.isBlank()) {
                    showError = "Please select both teams"
                    return@CalendarEntryDialog
                }
                if (start > end) {
                    showError = "Start time must be before End time"
                    return@CalendarEntryDialog
                }
                viewModel.updateCalendarEntry(
                    id = entry.id,
                    blueTeamId = blueTeamId,
                    orangeTeamId = orangeTeamId,
                    startTime = start,
                    endTime = end
                )
                entryToEdit = null
            },
            confirmLabel = "Save",
            onDismiss = { entryToEdit = null }
        )
    }

    // =============================================
    // Delete Entry Alert Dialog
    // =============================================
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

// =============================================
// Shared calendar-entry form dialog
// =============================================

@Composable
private fun CalendarEntryDialog(
    title: String,
    startDate: String,
    startTime: String,
    endDate: String,
    endTime: String,
    onStartDateChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    blueTeamId: String,
    orangeTeamId: String,
    onBlueTeamSelected: (String) -> Unit,
    onOrangeTeamSelected: (String) -> Unit,
    teams: List<com.kingsaul22.etxcenter.domain.model.Team>,
    errorMessage: String?,
    onConfirm: () -> Unit,
    confirmLabel: String,
    onDismiss: () -> Unit
) {
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
                // --- Start timestamp ---
                Text(
                    text = "Start",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                DateTimePickerField(
                    dateValue = startDate,
                    timeValue = startTime,
                    onDateChange = onStartDateChange,
                    onTimeChange = onStartTimeChange,
                    label = "Start"
                )

                // --- End timestamp ---
                Text(
                    text = "End",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                DateTimePickerField(
                    dateValue = endDate,
                    timeValue = endTime,
                    onDateChange = onEndDateChange,
                    onTimeChange = onEndTimeChange,
                    label = "End"
                )

                // --- Inline validation error ---
                errorMessage?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // --- Blue team selector ---
                SearchableTeamDropdown(
                    teams = teams,
                    selectedTeamId = blueTeamId,
                    onTeamSelected = onBlueTeamSelected,
                    label = "Blue Team"
                )

                // --- Orange team selector ---
                SearchableTeamDropdown(
                    teams = teams,
                    selectedTeamId = orangeTeamId,
                    onTeamSelected = onOrangeTeamSelected,
                    label = "Orange Team"
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// =============================================
// Date-time utilities
// =============================================

private fun parseDateTimeToEpoch(input: String): Long? {
    return try {
        val formatted = input.trim().replace(" ", "T")
        val isoString = formatted + if (formatted.length == 16) ":00" else ""
        val localDateTime = LocalDateTime.parse(isoString)
        localDateTime.toInstant(TimeZone.currentSystemDefault()).epochSeconds
    } catch (e: Exception) {
        null
    }
}

private fun formatEpochToDateTime(epochSeconds: Long): String {
    return try {
        val instant = Instant.fromEpochSeconds(epochSeconds)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val year = localDateTime.year
        val month = localDateTime.month.toString().padStart(2, '0')
        val day = localDateTime.day.toString().padStart(2, '0')
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        "$year-$month-$day $hour:$minute"
    } catch (e: Exception) {
        ""
    }
}

/**
 * Splits an epoch-seconds value into a date part ("YYYY-MM-DD") and
 * a time part ("HH:mm") for populating the split picker fields.
 */
private fun splitEpochToDateAndTime(epochSeconds: Long): Pair<String, String> {
    return try {
        val instant = Instant.fromEpochSeconds(epochSeconds)
        val ldt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val date = "${ldt.year}-${ldt.month.toString().padStart(2, '0')}-${ldt.day.toString().padStart(2, '0')}"
        val time = "${ldt.hour.toString().padStart(2, '0')}:${ldt.minute.toString().padStart(2, '0')}"
        date to time
    } catch (e: Exception) {
        "" to ""
    }
}
