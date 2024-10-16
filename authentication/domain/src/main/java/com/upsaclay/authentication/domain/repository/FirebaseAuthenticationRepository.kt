package com.upsaclay.authentication.domain.repository

interface FirebaseAuthenticationRepository {
    suspend fun loginWithEmailAndPassword(email: String, password: String): Result<Unit>

    suspend fun registerWithEmailAndPassword(email: String, password: String): Result<Unit>

    suspend fun logout(): Result<Unit>

    suspend fun sendVerificationEmail(): Result<Unit>

    fun isUserEmailVerified(): Boolean
}