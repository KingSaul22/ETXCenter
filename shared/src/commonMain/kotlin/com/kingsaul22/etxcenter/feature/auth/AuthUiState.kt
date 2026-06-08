package com.kingsaul22.etxcenter.feature.auth

sealed interface AuthUiState {
    data object Loading : AuthUiState
    data class Authenticated(val isAdmin: Boolean = false) : AuthUiState
    data class Error(val message: String) : AuthUiState
}