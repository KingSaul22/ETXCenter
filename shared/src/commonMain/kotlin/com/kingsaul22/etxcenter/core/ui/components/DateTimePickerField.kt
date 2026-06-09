package com.kingsaul22.etxcenter.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * A compound field that shows a date input and a time input side-by-side.
 *
 * Each input has a trailing icon:
 * - **Calendar icon** on the date field → opens a Material 3 [DatePickerDialog].
 * - **Clock icon** on the time field → opens a Material 3 [TimePickerDialog].
 *
 * The user can still type values manually.
 *
 * @param dateValue Current date text in "YYYY-MM-DD" format.
 * @param timeValue Current time text in "HH:mm" format.
 * @param onDateChange Callback when the date text changes (manual or from picker).
 * @param onTimeChange Callback when the time text changes (manual or from picker).
 * @param label Human-readable label such as "Start" or "End".
 * @param modifier Modifier for the root Row.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerField(
    dateValue: String,
    timeValue: String,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = dateValue,
            onValueChange = onDateChange,
            label = { Text("$label Date") },
            placeholder = { Text("YYYY-MM-DD") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Pick $label date"
                    )
                }
            },
            modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
            value = timeValue,
            onValueChange = onTimeChange,
            label = { Text("$label Time") },
            placeholder = { Text("HH:mm") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Pick $label time"
                    )
                }
            },
            modifier = Modifier.weight(1f)
        )
    }

    // ---- Date picker dialog ----
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            // DatePicker returns midnight UTC for the selected day
                            val ld = instant.toLocalDateTime(TimeZone.UTC)
                            val y = ld.year.toString()
                            val m = ld.month.toString().padStart(2, '0')
                            val d = ld.day.toString().padStart(2, '0')
                            onDateChange("$y-$m-$d")
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ---- Time picker dialog ----
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onConfirm = {
                val h = timePickerState.hour.toString().padStart(2, '0')
                val min = timePickerState.minute.toString().padStart(2, '0')
                onTimeChange("$h:$min")
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}
