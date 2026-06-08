package com.kingsaul22.etxcenter.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kingsaul22.etxcenter.feature.auth.AdminLoginScreen
import com.kingsaul22.etxcenter.feature.home.HomeScreen
import com.kingsaul22.etxcenter.feature.live.LiveScreen
import com.kingsaul22.etxcenter.feature.players.ManagePlayersScreen
import com.kingsaul22.etxcenter.feature.players.PlayersScreen
import com.kingsaul22.etxcenter.feature.stats.MatchDetailsScreen
import com.kingsaul22.etxcenter.feature.stats.MatchDetailsViewModel
import com.kingsaul22.etxcenter.feature.stats.StatsScreen
import com.kingsaul22.etxcenter.feature.teams.ManageTeamsScreen
import com.kingsaul22.etxcenter.feature.teams.TeamsScreen
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

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
        composable<TopLevelDestination.Home> {
            HomeScreen(
                onAdminClick = {
                    navController.navigate(AdminLoginDestination)
                },
                onManageTeamsClick = {
                    navController.navigate(ManageTeamsDestination)
                },
                onManagePlayersClick = {
                    navController.navigate(ManagePlayersDestination)
                }
            )
        }
        composable<TopLevelDestination.Live> { LiveScreen() }
        composable<TopLevelDestination.Stats> {
            StatsScreen(
                onMatchClick = { matchId ->
                    navController.navigate(MatchDetailsDestination(matchId))
                }
            )
        }
        composable<TopLevelDestination.Teams> { TeamsScreen() }
        composable<TopLevelDestination.Players> { PlayersScreen() }

        composable<MatchDetailsDestination> { backStackEntry ->
            val destination: MatchDetailsDestination = backStackEntry.toRoute()
            val viewModel: MatchDetailsViewModel = koinInject(
                parameters = { parametersOf(destination.matchId) }
            )
            MatchDetailsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<AdminLoginDestination> {
            AdminLoginScreen(
                onBackClick = { navController.popBackStack() },
                onLoginSuccess = { navController.popBackStack() }
            )
        }

        composable<ManageTeamsDestination> {
            ManageTeamsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<ManagePlayersDestination> {
            ManagePlayersScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
