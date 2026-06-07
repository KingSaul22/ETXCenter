package com.kingsaul22.etxcenter.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.AuthResult
import com.kingsaul22.etxcenter.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        authenticate()
    }

    fun authenticate() {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.signInSilently()) {
                is AuthResult.Success -> {
                    _uiState.value = AuthUiState.Authenticated
                }

                is AuthResult.Failure -> {
                    _uiState.value =
                        AuthUiState.Error(result.cause.message ?: "Unknown authentication error")
                }
            }
        }
    }
}