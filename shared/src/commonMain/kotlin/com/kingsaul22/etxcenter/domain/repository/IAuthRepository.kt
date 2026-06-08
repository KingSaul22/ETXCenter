package com.kingsaul22.etxcenter.domain.repository

import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    suspend fun signInSilently(): AuthResult
    
    suspend fun signInAsAdmin(email: String, password: String): Result<Unit>
    
    fun getIsAdminFlow(): Flow<Boolean>
    
    suspend fun signOutAdmin()
}