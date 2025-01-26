package com.upsaclay.authentication.domain.repository

interface FirebaseAuthenticationRepository {
    suspend fun loginWithEmailAndPassword(email: String, password: String)

    suspend fun registerWithEmailAndPassword(email: String, password: String): String

    suspend fun logout()

    suspend fun sendVerificationEmail()

    fun isUserEmailVerified(): Boolean
}