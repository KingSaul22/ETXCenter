package com.kingsaul22.etxcenter.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed interface TopLevelDestination {
    @Serializable data object Home : TopLevelDestination
    @Serializable data object Live : TopLevelDestination
    @Serializable data object Stats : TopLevelDestination
    @Serializable data object Teams : TopLevelDestination
    @Serializable data object Players : TopLevelDestination

    companion object {
        val entries: List<TopLevelDestination> = listOf(Home, Live, Stats, Teams, Players)
    }
}

val TopLevelDestination.label: String
    get() = when (this) {
        TopLevelDestination.Home -> "Home"
        TopLevelDestination.Live -> "Live"
        TopLevelDestination.Stats -> "Stats"
        TopLevelDestination.Teams -> "Teams"
        TopLevelDestination.Players -> "Players"
    }

val TopLevelDestination.icon: ImageVector
    get() = when (this) {
        TopLevelDestination.Home -> Icons.Default.Home
        TopLevelDestination.Live -> Icons.Default.PlayArrow
        TopLevelDestination.Stats -> Icons.AutoMirrored.Filled.List
        TopLevelDestination.Teams -> Icons.Default.Face
        TopLevelDestination.Players -> Icons.Default.Person
    }

@Serializable
data class MatchDetailsDestination(val matchId: String)

@Serializable
data object AdminLoginDestination

@Serializable
data object ManageTeamsDestination

@Serializable
data object ManagePlayersDestination

@Serializable
data object ManageMetadataDestination

