package com.kingsaul22.etxcenter.feature.auth

sealed interface AuthUiState {
    data object Loading : AuthUiState
    data object Authenticated : AuthUiState
    data class Error(val message: String) : AuthUiState
}