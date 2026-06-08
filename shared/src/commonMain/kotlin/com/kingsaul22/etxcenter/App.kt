package com.kingsaul22.etxcenter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.kingsaul22.etxcenter.core.theme.ETXCenterTheme
import com.kingsaul22.etxcenter.core.ui.layout.AdaptiveScaffold
import com.kingsaul22.etxcenter.core.ui.localization.AppStrings
import com.kingsaul22.etxcenter.core.ui.localization.Language
import com.kingsaul22.etxcenter.core.ui.localization.LocalLanguage
import com.kingsaul22.etxcenter.core.ui.localization.LocalLanguageController
import com.kingsaul22.etxcenter.core.ui.localization.LocalStrings
import com.kingsaul22.etxcenter.feature.auth.AuthUiState
import com.kingsaul22.etxcenter.feature.auth.AuthViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import org.koin.compose.koinInject
@Composable
fun App() {
    val viewModel: AuthViewModel = koinInject()
    val state by viewModel.uiState.collectAsState()
    
    var currentLanguage by rememberSaveable { mutableStateOf(Language.EN) }
    val strings = remember(currentLanguage) { AppStrings.get(currentLanguage) }

    CompositionLocalProvider(
        LocalLanguage provides currentLanguage,
        LocalStrings provides strings,
        LocalLanguageController provides { currentLanguage = it }
    ) {
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
                                text = "${strings.errorPrefix}$errorMessage",
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { viewModel.authenticate() }) {
                                Text(strings.retry)
                            }
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