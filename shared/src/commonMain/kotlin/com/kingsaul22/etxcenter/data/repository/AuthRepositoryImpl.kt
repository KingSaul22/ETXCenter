package com.kingsaul22.etxcenter.data.repository

import com.kingsaul22.etxcenter.domain.repository.AuthResult
import com.kingsaul22.etxcenter.domain.repository.IAuthRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase
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

    override suspend fun signInAsAdmin(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun signOutAdmin() {
        try {
            firebaseAuth.signOut()
            signInSilently()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getIsAdminFlow(): Flow<Boolean> {
        return firebaseAuth.authStateChanged.flatMapLatest { user ->
            if (user == null || user.isAnonymous) {
                flowOf(false)
            } else {
                database.reference("admins/${user.uid}")
                    .valueEvents
                    .map { dataSnapshot ->
                        if (dataSnapshot.exists) {
                            dataSnapshot.value<Boolean>() == true
                        } else {
                            false
                        }
                    }
                    .catch { e ->
                        e.printStackTrace()
                        emit(false)
                    }
            }
        }
    }
}