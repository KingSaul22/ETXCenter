package com.kingsaul22.etxcenter.data.repository

import com.kingsaul22.etxcenter.domain.repository.AuthResult
import com.kingsaul22.etxcenter.domain.repository.IAuthRepository
import dev.gitlive.firebase.auth.FirebaseAuth

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : IAuthRepository {

    override suspend fun signInSilently(): AuthResult {
        return try {
            if (firebaseAuth.currentUser == null) {
                firebaseAuth.signInAnonymously()
            }
            AuthResult.Success
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.Failure(e)
        }
    }
}