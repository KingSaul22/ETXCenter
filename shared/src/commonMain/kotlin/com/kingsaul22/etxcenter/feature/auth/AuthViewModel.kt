package com.kingsaul22.etxcenter.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.AuthResult
import com.kingsaul22.etxcenter.domain.repository.IAuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModel(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val authResult = MutableStateFlow<AuthResult?>(null)

    val uiState: StateFlow<AuthUiState> = authResult.flatMapLatest { result ->
        when (result) {
            null -> flowOf(AuthUiState.Loading)
            is AuthResult.Success -> authRepository.getIsAdminFlow().map { isAdmin ->
                AuthUiState.Authenticated(isAdmin = isAdmin)
            }
            is AuthResult.Failure -> flowOf(
                AuthUiState.Error(result.cause.message ?: "Unknown authentication error")
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthUiState.Loading
    )

    init {
        authenticate()
    }

    fun authenticate() {
        authResult.value = null
        viewModelScope.launch {
            authResult.value = authRepository.signInSilently()
        }
    }
}