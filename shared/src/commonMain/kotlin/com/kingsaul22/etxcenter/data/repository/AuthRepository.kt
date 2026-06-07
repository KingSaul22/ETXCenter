package com.kingsaul22.etxcenter.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = Firebase.auth
) {
    suspend fun signInSilently(): Boolean {
        return try {
            if (firebaseAuth.currentUser == null) {
                firebaseAuth.signInAnonymously()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}