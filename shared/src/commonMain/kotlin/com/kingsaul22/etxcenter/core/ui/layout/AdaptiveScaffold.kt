package com.kingsaul22.etxcenter.core.ui.layout

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kingsaul22.etxcenter.core.navigation.AppNavHost
import com.kingsaul22.etxcenter.core.navigation.TopLevelDestination
import com.kingsaul22.etxcenter.core.navigation.icon
import com.kingsaul22.etxcenter.core.navigation.label

@Composable
fun AdaptiveScaffold(navController: NavHostController) {
    BoxWithConstraints {
        val windowSizeClass = calculateWindowSizeClass()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

        CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
            Scaffold(
                bottomBar = {
                    if (isCompact) {
                        NavigationBar {
                            TopLevelDestination.entries.forEach { destination ->
                                NavigationBarItem(
                                    selected = currentRoute == destination::class.qualifiedName,
                                    onClick = {
                                        navController.navigate(destination) {
                                            popUpTo(TopLevelDestination.Home) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            destination.icon,
                                            contentDescription = destination.label
                                        )
                                    },
                                    label = { Text(destination.label) }
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                if (isCompact) {
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    )
                } else {
                    Row(Modifier.fillMaxSize()) {
                        NavigationRail {
                            TopLevelDestination.entries.forEach { destination ->
                                NavigationRailItem(
                                    selected = currentRoute == destination::class.qualifiedName,
                                    onClick = {
                                        navController.navigate(destination) {
                                            popUpTo(TopLevelDestination.Home) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            destination.icon,
                                            contentDescription = destination.label
                                        )
                                    },
                                    label = { Text(destination.label) }
                                )
                            }
                        }
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}