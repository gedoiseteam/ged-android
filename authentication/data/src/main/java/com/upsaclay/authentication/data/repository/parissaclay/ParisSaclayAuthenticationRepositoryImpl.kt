package com.upsaclay.authentication.data.repository.parissaclay

import com.upsaclay.authentication.data.remote.parissaclay.ParisSaclayAuthenticationRemoteDataSource

internal class ParisSaclayAuthenticationRepositoryImpl(
    private val remoteDataSource: ParisSaclayAuthenticationRemoteDataSource
): ParisSaclayAuthenticationRepository {
    override suspend fun loginWithParisSaclay(email: String, password: String, hash: String) {
        remoteDataSource.loginWithParisSaclay(email, password, hash)
    }
}