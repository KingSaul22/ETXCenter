package com.kingsaul22.etxcenter.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kingsaul22.etxcenter.domain.model.Team

/**
 * A dropdown field that lets the user **search** the team list by name.
 *
 * Uses the Material 3 [ExposedDropdownMenuBox] API, with the text field
 * acting as both the search filter and the selected-value display.
 *
 * @param teams         Full list of available teams.
 * @param selectedTeamId Currently selected team ID, or blank if none.
 * @param onTeamSelected Called when the user picks a team from the dropdown.
 * @param label          Label text shown on the field (e.g. "Blue Team").
 * @param modifier       Modifier for the root [ExposedDropdownMenuBox].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableTeamDropdown(
    teams: List<Team>,
    selectedTeamId: String,
    onTeamSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember(selectedTeamId) {
        mutableStateOf(teams.find { it.id == selectedTeamId }?.name.orEmpty())
    }

    val filteredTeams = remember(searchQuery, teams) {
        if (searchQuery.isBlank()) {
            teams
        } else {
            teams.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                expanded = true
            },
            label = { Text(label) },
            placeholder = { Text("Search team…") },
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            if (filteredTeams.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No teams found") },
                    onClick = {},
                    enabled = false
                )
            } else {
                filteredTeams.forEach { team ->
                    DropdownMenuItem(
                        text = { Text(team.name) },
                        onClick = {
                            searchQuery = team.name
                            onTeamSelected(team.id)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}
