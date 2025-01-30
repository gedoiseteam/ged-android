package com.upsaclay.authentication.data.repository

import com.upsaclay.authentication.data.local.AuthenticationLocalDataSource
import com.upsaclay.authentication.data.remote.AuthenticationRemoteDataSource
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class AuthenticationRepositoryImpl(
    private val authenticationRemoteDataSource: AuthenticationRemoteDataSource,
    private val authenticationLocalDataSource: AuthenticationLocalDataSource,
    scope: CoroutineScope
) : AuthenticationRepository {
    private val _isAuthenticated = MutableStateFlow<Boolean?>(null)
    override val isAuthenticated: Flow<Boolean?> = _isAuthenticated

    init {
        scope.launch {
            authenticationLocalDataSource.getAuthenticationState().collect {
                _isAuthenticated.value = it
            }
        }
    }

    override suspend fun loginWithParisSaclay(email: String, password: String, hash: String) {
        authenticationRemoteDataSource.loginWithParisSaclay(email, password, hash)
    }

    override suspend fun setAuthenticated(isAuthenticated: Boolean) {
        authenticationLocalDataSource.setAuthenticationState(isAuthenticated)
    }
}