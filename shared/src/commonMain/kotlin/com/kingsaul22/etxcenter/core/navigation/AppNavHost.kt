package com.kingsaul22.etxcenter.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kingsaul22.etxcenter.feature.home.HomeScreen
import com.kingsaul22.etxcenter.feature.live.LiveScreen
import com.kingsaul22.etxcenter.feature.players.PlayersScreen
import com.kingsaul22.etxcenter.feature.stats.StatsScreen
import com.kingsaul22.etxcenter.feature.teams.TeamsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = TopLevelDestination.Home,
        modifier = modifier
    ) {
        composable<TopLevelDestination.Home> { HomeScreen() }
        composable<TopLevelDestination.Live> { LiveScreen() }
        composable<TopLevelDestination.Stats> { StatsScreen() }
        composable<TopLevelDestination.Teams> { TeamsScreen() }
        composable<TopLevelDestination.Players> { PlayersScreen() }
    }
}
