package com.upsaclay.authentication.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthenticationRepository {
    val isAuthenticated: StateFlow<Boolean>

    suspend fun loginWithParisSaclay(email: String, password: String, hash: String): Result<Unit>

    suspend fun setAuthenticated(isAuthenticated: Boolean)
}