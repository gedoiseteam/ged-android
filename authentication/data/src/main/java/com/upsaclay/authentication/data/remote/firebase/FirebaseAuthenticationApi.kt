package com.upsaclay.authentication.data.remote.firebase

interface FirebaseAuthenticationApi {
    suspend fun signInWithEmailAndPassword(email: String, password: String)

    suspend fun signUpWithEmailAndPassword(email: String, password: String): String

    suspend fun signOut()

    suspend fun sendVerificationEmail()

    suspend fun isUserEmailVerified(): Boolean
}