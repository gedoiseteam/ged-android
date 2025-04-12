package com.upsaclay.common.data.local

import com.upsaclay.common.domain.entity.FcmToken
import kotlinx.coroutines.flow.Flow

class FCMLocalDataSource(
    private val fcmDataStore: FCMDataStore
) {
    fun getUnsentFcmToken(): Flow<FcmToken?> = fcmDataStore.getFcmToken()

    suspend fun storeUnsentFcmToken(fcmToken: FcmToken) {
        fcmDataStore.storeFcmToken(fcmToken)
    }

    suspend fun removeUnsentFcmToken() {
        fcmDataStore.removeFcmToken()
    }
}