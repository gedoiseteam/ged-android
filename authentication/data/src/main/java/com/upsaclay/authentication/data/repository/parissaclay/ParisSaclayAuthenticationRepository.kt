package com.upsaclay.authentication.data.repository.parissaclay

internal interface ParisSaclayAuthenticationRepository {
    suspend fun loginWithParisSaclay(email: String, password: String, hash: String)
}