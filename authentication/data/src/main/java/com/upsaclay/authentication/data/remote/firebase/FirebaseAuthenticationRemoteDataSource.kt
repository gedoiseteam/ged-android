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
    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        withContext(Dispatchers.IO) {
            try {
                firebaseAuthenticationApi.signInWithEmailAndPassword(email, password)
            } catch (e: FirebaseAuthException) {
                e("Error to sign in with email and password with Firebase: ${e.message}", e)
                throw e
            } catch (e: FirebaseNetworkException) {
                e("Error network connection ${e.message}", e)
                throw e
            } catch (e: FirebaseTooManyRequestsException) {
                e("Error to sign in with email and password with Firebase: ${e.message}", e)
                throw e
            }
        }
    }

    suspend fun signUpWithEmailAndPassword(email: String, password: String): String =
        withContext(Dispatchers.IO) {
            try {
                firebaseAuthenticationApi.signUpWithEmailAndPassword(email, password)
            } catch (e: FirebaseAuthException) {
                e("Error to sign up with email and password with Firebase: ${e.message}", e)
                throw e
            } catch (e: FirebaseNetworkException) {
                e("Error network connection ${e.message}", e)
                throw e
            } catch (e: FirebaseTooManyRequestsException) {
                e("Error to sign up with email and password with Firebase: ${e.message}", e)
                throw e
            }
        }

    suspend fun signOut() {
        withContext(Dispatchers.IO) {
            try {
                firebaseAuthenticationApi.signOut()
            } catch (e: FirebaseAuthException) {
                e("Error to logout with Firebase: ${e.message}", e)
                throw e
            } catch (e: FirebaseNetworkException) {
                e("Error network connection ${e.message}", e)
                throw e
            } catch (e: FirebaseTooManyRequestsException) {
                e("Error to logout with Firebase: ${e.message}", e)
                throw e
            }
        }
    }

    suspend fun sendVerificationEmail() {
        withContext(Dispatchers.IO) {
            try {
                firebaseAuthenticationApi.sendVerificationEmail()
            } catch (e: FirebaseAuthException) {
                e("Error to send verification email with Firebase: ${e.message}", e)
                throw e
            } catch (e: FirebaseNetworkException) {
                e("Error network connection ${e.message}", e)
                throw e
            } catch (e: FirebaseTooManyRequestsException) {
                e("Error to send verification email with Firebase: ${e.message}", e)
                throw e
            }
        }
    }

    fun isUserEmailVerified(): Boolean = firebaseAuthenticationApi.isUserEmailVerified()
}