package com.upsaclay.gedoise.data.repository

import com.upsaclay.gedoise.domain.repository.CredentialsRepository
import com.upsaclay.gedoise.data.api.CredentialsApi
import com.upsaclay.gedoise.data.local.CredentialsLocalDataSource
import com.upsaclay.gedoise.domain.entities.FcmToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class CredentialsRepositoryImpl(
    private val credentialsLocalDataSource: CredentialsLocalDataSource,
    private val credentialsApi: CredentialsApi
): CredentialsRepository {
    override val fcmToken: Flow<FcmToken?> = credentialsLocalDataSource.getUnsentFcmToken()

    override suspend fun sendFcmToken(token: FcmToken) {
        withContext(Dispatchers.IO) {
            val r = credentialsApi.addFcmToken(token.userId, token.value)
            r.isSuccessful
        }
    }

    override suspend fun storeUnsentFcmToken(token: FcmToken) {
        credentialsLocalDataSource.storeUnsentFcmToken(token)
    }

    override suspend fun removeUnsentFcmToken() {
        credentialsLocalDataSource.removeUnsentFcmToken()
    }
}