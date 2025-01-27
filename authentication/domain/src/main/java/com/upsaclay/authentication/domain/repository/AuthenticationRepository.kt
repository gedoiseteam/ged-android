package com.upsaclay.authentication.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    val isAuthenticated: Flow<Boolean?>

    suspend fun loginWithParisSaclay(email: String, password: String, hash: String)

    suspend fun setAuthenticated(isAuthenticated: Boolean)
}