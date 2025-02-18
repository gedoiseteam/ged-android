package com.upsaclay.authentication.data.remote.firebase

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.upsaclay.authentication.domain.entity.exception.AuthErrorCode
import com.upsaclay.authentication.domain.entity.exception.AuthenticationException
import com.upsaclay.authentication.domain.entity.exception.UserAlreadyExistsException
import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.entity.ServerCommunicationException
import com.upsaclay.common.domain.entity.TooManyRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class FirebaseAuthenticationRemoteDataSource(
    private val firebaseAuthenticationApi: FirebaseAuthenticationApi
) {
    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        withContext(Dispatchers.IO) {
            try {
                firebaseAuthenticationApi.signInWithEmailAndPassword(email, password)
            } catch (e: FirebaseAuthException) {
                e("Error to sign in with email and password with Firebase: ${e.message}", e)
                when (AuthErrorCode.fromCode(e.errorCode)) {
                    AuthErrorCode.EMAIL_ALREADY_AFFILIATED -> throw UserAlreadyExistsException()
                    AuthErrorCode.INVALID_CREDENTIALS -> throw AuthenticationException()
                    else -> throw IOException()
                }
            } catch (e: FirebaseNetworkException) {
                e("Error to sign in with email and password with Firebase because of network connection ${e.message}", e)
                throw ServerCommunicationException()
            } catch (e: FirebaseTooManyRequestsException) {
                e("Error to sign in with email and password with Firebase: ${e.message}", e)
                throw TooManyRequestException()
            }
        }
    }

    suspend fun signUpWithEmailAndPassword(email: String, password: String): String =
        withContext(Dispatchers.IO) {
            try {
                firebaseAuthenticationApi.signUpWithEmailAndPassword(email, password)
            } catch (e: FirebaseAuthException) {
                e("Error to sign up with email and password with Firebase: ${e.message}", e)
                when (AuthErrorCode.fromCode(e.errorCode)) {
                    AuthErrorCode.EMAIL_ALREADY_AFFILIATED -> throw UserAlreadyExistsException()
                    AuthErrorCode.INVALID_CREDENTIALS -> throw AuthenticationException()
                    else -> throw IOException()
                }
            } catch (e: FirebaseNetworkException) {
                e("Error to sign up with email and password with Firebase because of network connection ${e.message}", e)
                throw ServerCommunicationException()
            } catch (e: FirebaseTooManyRequestsException) {
                e("Error to sign up with email and password with Firebase: ${e.message}", e)
                throw TooManyRequestException()
            }
        }

    suspend fun signOut() {
        withContext(Dispatchers.IO) {
            try {
                firebaseAuthenticationApi.signOut()
            } catch (e: FirebaseAuthException) {
                e("Error to logout with Firebase: ${e.message}", e)
            } catch (e: FirebaseNetworkException) {
                e("Error network connection ${e.message}", e)
            } catch (e: FirebaseTooManyRequestsException) {
                e("Error to logout with Firebase: ${e.message}", e)
            }
        }
    }

    suspend fun sendVerificationEmail() {
        withContext(Dispatchers.IO) {
            try {
                firebaseAuthenticationApi.sendVerificationEmail()
            } catch (e: FirebaseNetworkException) {
                e("Error network connection ${e.message}", e)
                throw ServerCommunicationException()
            } catch (e: FirebaseTooManyRequestsException) {
                e("Error to send verification email with Firebase: ${e.message}", e)
                throw TooManyRequestException()
            }
        }
    }

    suspend fun isUserEmailVerified(): Boolean = firebaseAuthenticationApi.isUserEmailVerified()
}