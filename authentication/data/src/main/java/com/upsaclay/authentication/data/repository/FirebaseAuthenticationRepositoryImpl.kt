package com.upsaclay.authentication.data.repository

import android.security.keystore.UserNotAuthenticatedException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.upsaclay.authentication.data.remote.firebase.FirebaseAuthenticationRemoteDataSource
import com.upsaclay.authentication.domain.entity.exception.AuthenticationException
import com.upsaclay.authentication.domain.entity.exception.FirebaseAuthErrorCode
import com.upsaclay.authentication.domain.entity.exception.TooManyRequestException
import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository
import com.upsaclay.common.domain.entity.exception.NetworkException

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