package com.upsaclay.authentication.data.remote

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthenticationApiImpl: FirebaseAuthenticationApi {
    private val firebaseAuth = Firebase.auth

    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        suspendCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
        }
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String) {
        suspendCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
        }
    }

    override suspend fun signOut() {
        suspendCoroutine { continuation ->
            firebaseAuth.signOut()
            continuation.resume(Unit)
        }
    }

    override suspend fun sendVerificationEmail() {
        suspendCoroutine { continuation ->
            firebaseAuth.currentUser?.let { currentUser ->
                currentUser.reload()
                currentUser.sendEmailVerification()
                    .addOnSuccessListener { continuation.resume(Unit) }
                    .addOnFailureListener { e -> continuation.resumeWithException(e) }
            } ?: continuation.resumeWithException(FirebaseAuthInvalidUserException("ERROR_USER_NOT_FOUND", "Firebase auth current user is null"))
        }
    }

    override suspend fun isUserEmailVerified(): Boolean = suspendCoroutine { continuation ->
        firebaseAuth.currentUser?.let { currentUser ->
            currentUser.reload()
                .addOnSuccessListener { continuation.resume(currentUser.isEmailVerified) }
                .addOnFailureListener { continuation.resume(false) }
        } ?: continuation.resumeWithException(FirebaseAuthInvalidUserException("ERROR_USER_NOT_FOUND", "Firebase auth current user is null"))
    }

    override fun isAuthenticated(): Boolean = firebaseAuth.currentUser != null
}