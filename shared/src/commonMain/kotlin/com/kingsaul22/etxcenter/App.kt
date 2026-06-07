package com.kingsaul22.etxcenter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.kingsaul22.etxcenter.core.theme.ETXCenterTheme
import com.kingsaul22.etxcenter.core.ui.layout.AdaptiveScaffold
import com.kingsaul22.etxcenter.feature.auth.AuthUiState
import com.kingsaul22.etxcenter.feature.auth.AuthViewModel
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val viewModel: AuthViewModel = koinInject()
    val state by viewModel.uiState.collectAsState()

    ETXCenterTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (state) {
                is AuthUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is AuthUiState.Authenticated -> {
                    ETXCenterApp()
                }

                is AuthUiState.Error -> {
                    val errorMessage = (state as AuthUiState.Error).message
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Connection Failed: $errorMessage",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.authenticate() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ETXCenterApp() {
    val navController = rememberNavController()
    AdaptiveScaffold(navController = navController)
}