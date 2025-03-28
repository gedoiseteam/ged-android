package com.upsaclay.authentication.data.repository

import com.upsaclay.authentication.data.local.AuthenticationLocalDataSource
import com.upsaclay.authentication.data.repository.firebase.FirebaseAuthenticationRepository
import com.upsaclay.authentication.data.repository.parissaclay.ParisSaclayAuthenticationRepository
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class AuthenticationRepositoryImpl(
    private val parisSaclayAuthenticationRepository: ParisSaclayAuthenticationRepository,
    private val firebaseAuthenticationRepository: FirebaseAuthenticationRepository,
    private val authenticationLocalDataSource: AuthenticationLocalDataSource,
    private val scope: CoroutineScope
) : AuthenticationRepository {
    private val _isAuthenticated = MutableStateFlow<Boolean?>(null)
    override val isAuthenticated: Flow<Boolean?> = _isAuthenticated

    init {
        scope.launch {
            if (!firebaseAuthenticationRepository.isAuthenticated()) {
                setAuthenticated(false)
            }
            authenticationLocalDataSource.getAuthenticationState().collect {
                _isAuthenticated.value = it
            }
        }
    }

    override suspend fun loginWithParisSaclay(email: String, password: String, hash: String) {
        parisSaclayAuthenticationRepository.loginWithParisSaclay(email, password, hash)
    }

    override suspend fun loginWithEmailAndPassword(email: String, password: String) {
        firebaseAuthenticationRepository.loginWithEmailAndPassword(email, password)
    }

    override suspend fun registerWithEmailAndPassword(email: String, password: String) {
        return firebaseAuthenticationRepository.registerWithEmailAndPassword(email, password)
    }

    override suspend fun logout() {
        scope.launch { firebaseAuthenticationRepository.logout() }
        setAuthenticated(false)
    }

    override suspend fun sendVerificationEmail() {
        firebaseAuthenticationRepository.sendVerificationEmail()
    }

    override suspend fun isUserEmailVerified(): Boolean = firebaseAuthenticationRepository.isUserEmailVerified()

    override suspend fun setAuthenticated(isAuthenticated: Boolean) {
        authenticationLocalDataSource.setAuthenticationState(isAuthenticated)
    }
}