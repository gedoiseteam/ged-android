package com.upsaclay.gedoise.data.local

import com.upsaclay.gedoise.domain.entities.FcmToken
import kotlinx.coroutines.flow.Flow

class CredentialsLocalDataSource(
    private val credentialsDataStore: CredentialsDataStore
) {
    fun getUnsentFcmToken(): Flow<FcmToken?> = credentialsDataStore.getFcmToken()

    suspend fun storeUnsentFcmToken(fcmToken: FcmToken) {
        credentialsDataStore.storeFcmToken(fcmToken)
    }

    suspend fun removeUnsentFcmToken() {
        credentialsDataStore.removeFcmToken()
    }
}