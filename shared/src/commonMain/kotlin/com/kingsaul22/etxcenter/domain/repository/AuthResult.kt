package com.kingsaul22.etxcenter.domain.repository

sealed interface AuthResult {
    data object Success : AuthResult
    data class Failure(val cause: Throwable) : AuthResult
}