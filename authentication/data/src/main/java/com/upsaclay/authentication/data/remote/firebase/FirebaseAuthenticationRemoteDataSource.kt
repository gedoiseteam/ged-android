package com.upsaclay.authentication.data.remote.firebase

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.upsaclay.common.domain.e
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseAuthenticationRemoteDataSource(
    private val firebaseAuthenticationApi: FirebaseAuthenticationApi
) {
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firebaseAuthenticationApi.signInWithEmailAndPassword(email, password)
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            e("Error to sign in with email and password with Firebase: ${e.message}", e)
            Result.failure(e)
        } catch (e: FirebaseNetworkException) {
            e("Error network connection ${e.message}", e)
            Result.failure(e)
        } catch (e: FirebaseTooManyRequestsException) {
            e("Error to sign in with email and password with Firebase: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmailAndPassword(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firebaseAuthenticationApi.signUpWithEmailAndPassword(email, password)
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            e("Error to sign up with email and password with Firebase: ${e.message}", e)
            Result.failure(e)
        } catch (e: FirebaseNetworkException) {
            e("Error network connection ${e.message}", e)
            Result.failure(e)
        } catch (e: FirebaseTooManyRequestsException) {
            e("Error to sign up with email and password with Firebase: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firebaseAuthenticationApi.signOut()
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            e("Error to logout with Firebase: ${e.message}", e)
            Result.failure(e)
        } catch (e: FirebaseNetworkException) {
            e("Error network connection ${e.message}", e)
            Result.failure(e)
        } catch (e: FirebaseTooManyRequestsException) {
            e("Error to logout with Firebase: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun sendVerificationEmail(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firebaseAuthenticationApi.sendVerificationEmail()
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            e("Error to send verification email with Firebase: ${e.message}", e)
            Result.failure(e)
        } catch (e: FirebaseNetworkException) {
            e("Error network connection ${e.message}", e)
            Result.failure(e)
        } catch (e: FirebaseTooManyRequestsException) {
            e("Error to send verification email with Firebase: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun isUserEmailVerified(): Boolean = firebaseAuthenticationApi.isUserEmailVerified()
}