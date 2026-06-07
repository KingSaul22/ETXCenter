package com.kingsaul22.etxcenter.domain.repository

interface IAuthRepository {
    suspend fun signInSilently(): AuthResult
}