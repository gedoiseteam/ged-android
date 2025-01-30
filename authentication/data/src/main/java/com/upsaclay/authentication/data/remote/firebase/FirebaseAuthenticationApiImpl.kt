package com.upsaclay.authentication.data.remote.firebase

import android.security.keystore.UserNotAuthenticatedException
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthenticationApiImpl : FirebaseAuthenticationApi {
    private val firebaseAuth = Firebase.auth

    override suspend fun signInWithEmailAndPassword(email: String, password: String) =
        suspendCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
        }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String) =
        suspendCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.let {
                        continuation.resume(it.uid)
                    } ?: throw UserNotAuthenticatedException("User not found")
                }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
        }

    override suspend fun signOut() = suspendCoroutine { continuation ->
        firebaseAuth.signOut()
        continuation.resume(Unit)
    }

    override suspend fun sendVerificationEmail() = suspendCoroutine { continuation ->
        val currentUser = firebaseAuth.currentUser
            ?: return@suspendCoroutine continuation.resumeWithException(
                UserNotAuthenticatedException("User not found")
            )

        currentUser.reload()
        currentUser.sendEmailVerification()
            .addOnSuccessListener { continuation.resume(Unit) }
            .addOnFailureListener { e -> continuation.resumeWithException(e) }
    }

    override fun isUserEmailVerified(): Boolean {
        return firebaseAuth.currentUser?.let {
            it.reload()
            it.isEmailVerified
        } ?: false
    }
}