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
import org.koin.compose.viewmodel.koinViewModel

import com.kingsaul22.etxcenter.core.ui.localization.LocalStrings
import com.kingsaul22.etxcenter.core.ui.components.EtxTopAppBar
import com.kingsaul22.etxcenter.core.ui.components.MatchOverviewCard
import com.kingsaul22.etxcenter.core.ui.components.StandardConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCalendarScreen(
    onBackClick: () -> Unit,
    viewModel: ManageCalendarViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val strings = LocalStrings.current

    var showCreateDialog by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<CalendarEntry?>(null) }
    var entryToDelete by remember { mutableStateOf<CalendarEntry?>(null) }



    Scaffold(
        topBar = {
            EtxTopAppBar(
                title = strings.manageCalendarTitle,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = strings.addCalendarDesc)
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
                        text = strings.emptyCalendarMsg,
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

                        MatchOverviewCard(
                            blueTeamName = blueName,
                            orangeTeamName = orangeName,
                            topContent = {
                                Text(
                                    text = "${strings.windowLabel}: $startStr - $endStr",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingContent = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(onClick = { entryToEdit = entry }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = strings.editCalendarDesc,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = { entryToDelete = entry }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = strings.delete,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            },
                            bottomContent = {
                                if (entry.matchIds.isNotEmpty()) {
                                    Text(
                                        text = strings.playedMatchesLabel,
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
                        )
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
            title = strings.createCalendarTitle,
            initialEntry = null,
            teams = state.teams,
            onConfirm = { bId, oId, start, end ->
                viewModel.createCalendarEntry(
                    blueTeamId = bId,
                    orangeTeamId = oId,
                    startTime = start,
                    endTime = end
                )
                showCreateDialog = false
            },
            confirmLabel = strings.create,
            onDismiss = { showCreateDialog = false }
        )
    }

    // =============================================
    // Edit Entry Dialog
    // =============================================
    entryToEdit?.let { entry ->
        CalendarEntryDialog(
            title = strings.editCalendarTitle,
            initialEntry = entry,
            teams = state.teams,
            onConfirm = { bId, oId, start, end ->
                viewModel.updateCalendarEntry(
                    id = entry.id,
                    blueTeamId = bId,
                    orangeTeamId = oId,
                    startTime = start,
                    endTime = end
                )
                entryToEdit = null
            },
            confirmLabel = strings.save,
            onDismiss = { entryToEdit = null }
        )
    }

    // =============================================
    // Delete Entry Alert Dialog
    // =============================================
    entryToDelete?.let { entry ->
        StandardConfirmDialog(
            title = strings.deleteCalendarConfirmTitle,
            message = strings.deleteCalendarConfirmMsg,
            confirmText = strings.delete,
            dismissText = strings.cancel,
            isDestructive = true,
            onConfirm = {
                viewModel.deleteCalendarEntry(entry.id)
                entryToDelete = null
            },
            onDismiss = { entryToDelete = null }
        )
    }
}

// =============================================
// Shared calendar-entry form dialog
// =============================================

@Composable
private fun CalendarEntryDialog(
    title: String,
    initialEntry: CalendarEntry?,
    teams: List<com.kingsaul22.etxcenter.domain.model.Team>,
    onConfirm: (blueTeamId: String, orangeTeamId: String, startTimeEpoch: Long, endTimeEpoch: Long) -> Unit,
    confirmLabel: String,
    onDismiss: () -> Unit
) {
    val strings = LocalStrings.current

    var blueTeamId by remember(initialEntry) { mutableStateOf(initialEntry?.blueTeamId ?: "") }
    var orangeTeamId by remember(initialEntry) { mutableStateOf(initialEntry?.orangeTeamId ?: "") }

    var startDate by remember(initialEntry) { mutableStateOf(if (initialEntry != null) splitEpochToDateAndTime(initialEntry.startTime.epochSeconds).first else "") }
    var startTime by remember(initialEntry) { mutableStateOf(if (initialEntry != null) splitEpochToDateAndTime(initialEntry.startTime.epochSeconds).second else "") }
    var endDate by remember(initialEntry) { mutableStateOf(if (initialEntry != null) splitEpochToDateAndTime(initialEntry.endTime.epochSeconds).first else "") }
    var endTime by remember(initialEntry) { mutableStateOf(if (initialEntry != null) splitEpochToDateAndTime(initialEntry.endTime.epochSeconds).second else "") }

    var errorMessage by remember(initialEntry) { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

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
                    text = strings.startLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                DateTimePickerField(
                    dateValue = startDate,
                    timeValue = startTime,
                    onDateChange = { startDate = it },
                    onTimeChange = { startTime = it },
                    label = strings.startLabel
                )

                // --- End timestamp ---
                Text(
                    text = strings.endLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                DateTimePickerField(
                    dateValue = endDate,
                    timeValue = endTime,
                    onDateChange = { endDate = it },
                    onTimeChange = { endTime = it },
                    label = strings.endLabel
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
                    onTeamSelected = { blueTeamId = it },
                    label = strings.blueTeamLabel
                )

                // --- Orange team selector ---
                SearchableTeamDropdown(
                    teams = teams,
                    selectedTeamId = orangeTeamId,
                    onTeamSelected = { orangeTeamId = it },
                    label = strings.orangeTeamLabel
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isSubmitting) return@TextButton
                    val start = parseDateTimeToEpoch("$startDate $startTime")
                    val end = parseDateTimeToEpoch("$endDate $endTime")
                    if (start == null || end == null) {
                        errorMessage = strings.invalidDateTimeMsg
                        return@TextButton
                    }
                    if (blueTeamId.isBlank() || orangeTeamId.isBlank()) {
                        errorMessage = strings.selectBothTeamsMsg
                        return@TextButton
                    }
                    if (start > end) {
                        errorMessage = strings.startBeforeEndMsg
                        return@TextButton
                    }
                    isSubmitting = true
                    onConfirm(blueTeamId, orangeTeamId, start, end)
                },
                enabled = !isSubmitting
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
