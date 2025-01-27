package com.upsaclay.authentication.data.repository

import com.upsaclay.authentication.data.remote.firebase.FirebaseAuthenticationRemoteDataSource
import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository

class FirebaseAuthenticationRepositoryImpl(
    private val firebaseAuthenticationRemoteDataSource: FirebaseAuthenticationRemoteDataSource
): FirebaseAuthenticationRepository {
    override suspend fun loginWithEmailAndPassword(email: String, password: String) {
        firebaseAuthenticationRemoteDataSource.signInWithEmailAndPassword(email, password)
    }

    override suspend fun registerWithEmailAndPassword(email: String, password: String): String =
        firebaseAuthenticationRemoteDataSource.signUpWithEmailAndPassword(email, password)

    override suspend fun logout() {
       firebaseAuthenticationRemoteDataSource.signOut()
    }

    override suspend fun sendVerificationEmail() {
        firebaseAuthenticationRemoteDataSource.sendVerificationEmail()
    }

    override fun isUserEmailVerified(): Boolean = firebaseAuthenticationRemoteDataSource.isUserEmailVerified()
}