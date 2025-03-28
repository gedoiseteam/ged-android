package com.upsaclay.authentication.data.repository.firebase

interface FirebaseAuthenticationRepository {
    suspend fun loginWithEmailAndPassword(email: String, password: String)

    suspend fun registerWithEmailAndPassword(email: String, password: String)

    suspend fun logout()

    suspend fun sendVerificationEmail()

    suspend fun isUserEmailVerified(): Boolean

    fun isAuthenticated(): Boolean
}