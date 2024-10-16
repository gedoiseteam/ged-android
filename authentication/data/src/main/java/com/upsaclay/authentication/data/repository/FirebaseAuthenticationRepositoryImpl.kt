package com.upsaclay.authentication.data.repository

import android.security.keystore.UserNotAuthenticatedException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.upsaclay.authentication.data.remote.firebase.FirebaseAuthenticationRemoteDataSource
import com.upsaclay.authentication.domain.model.exception.AuthenticationException
import com.upsaclay.authentication.domain.model.exception.FirebaseAuthErrorCode
import com.upsaclay.authentication.domain.model.exception.TooManyRequestException
import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository
import com.upsaclay.common.domain.model.exception.NetworkException

class FirebaseAuthenticationRepositoryImpl(
    private val firebaseAuthenticationRemoteDataSource: FirebaseAuthenticationRemoteDataSource
): FirebaseAuthenticationRepository {
    override suspend fun loginWithEmailAndPassword(email: String, password: String): Result<Unit> {
        val result = firebaseAuthenticationRemoteDataSource.signInWithEmailAndPassword(email, password)
        result.onFailure { e ->
            return when(e) {
                is FirebaseNetworkException -> Result.failure(NetworkException("Error network connection ${e.message}", e))

                is FirebaseAuthInvalidCredentialsException -> {
                    Result.failure(
                        AuthenticationException(
                            "Error to sign in with email and password with Firebase: ${e.message}",
                            e,
                            FirebaseAuthErrorCode.INVALID_CREDENTIALS
                        )
                    )
                }

                is FirebaseTooManyRequestsException -> {
                    Result.failure(
                        TooManyRequestException("Error to sign in with email and password with Firebase: ${e.message}",e,)
                    )
                }

                is FirebaseAuthException -> {
                    Result.failure(
                        AuthenticationException("Error to sign in with email and password with Firebase: ${e.message}", e)
                    )
                }

                else -> Result.failure(e)
            }
        }
        return result
    }

    override suspend fun registerWithEmailAndPassword(email: String, password: String): Result<Unit> {
        val result = firebaseAuthenticationRemoteDataSource.signUpWithEmailAndPassword(email, password)
        result.onFailure { e ->
            return when(e) {
                is FirebaseNetworkException -> Result.failure(NetworkException("Error network connection ${e.message}", e))

                is FirebaseAuthException -> {
                    val errorCode = FirebaseAuthErrorCode.fromCode(e.errorCode)
                    Result.failure(
                        AuthenticationException("Error to sign up with email and password with Firebase: ${e.message}", e, errorCode)
                    )
                }

                is FirebaseTooManyRequestsException ->
                    Result.failure(
                        TooManyRequestException("Error to sign up with email and password with Firebase: ${e.message}", e)
                    )

                else -> Result.failure(e)
            }
        }
        return result
    }

    override suspend fun logout(): Result<Unit> {
        val result = firebaseAuthenticationRemoteDataSource.signOut()
        result.onFailure { e ->
            return when(e) {
                is FirebaseNetworkException ->
                    Result.failure(NetworkException("Error network connection ${e.message}", e))

                is FirebaseAuthException ->
                    Result.failure(AuthenticationException("Error to sign out with Firebase: ${e.message}", e))

                else -> Result.failure(e)
            }
        }
        return result
    }

    override suspend fun sendVerificationEmail(): Result<Unit> {
        val result = firebaseAuthenticationRemoteDataSource.sendVerificationEmail()
        result.onFailure { e ->
            return when(e) {
                is FirebaseNetworkException ->
                    Result.failure(NetworkException("Error network connection ${e.message}", e))

                is FirebaseTooManyRequestsException ->
                    Result.failure(TooManyRequestException("Error to send verification email with Firebase: ${e.message}", e))

                is UserNotAuthenticatedException ->
                    Result.failure(
                        AuthenticationException("Error to send verification email with Firebase: ${e.message}", e)
                    )

                else -> Result.failure(e)
            }
        }
        return result
    }

    override fun isUserEmailVerified(): Boolean = firebaseAuthenticationRemoteDataSource.isUserEmailVerified()
}