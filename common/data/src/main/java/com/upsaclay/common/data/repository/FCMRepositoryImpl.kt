package com.upsaclay.common.data.repository

import com.google.gson.Gson
import com.upsaclay.common.data.local.FCMLocalDataSource
import com.upsaclay.common.data.remote.api.FCMApi
import com.upsaclay.common.domain.entity.FcmToken
import com.upsaclay.common.domain.repository.FCMRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FCMRepositoryImpl(
    private val fcmLocalDataSource: FCMLocalDataSource,
    private val fcmApi: FCMApi
): FCMRepository {
    override val fcmToken: Flow<FcmToken?> = fcmLocalDataSource.getUnsentFcmToken()

    override suspend fun sendFcmToken(token: FcmToken) {
        withContext(Dispatchers.IO) {
            val r = fcmApi.addFcmToken(token.userId, token.value)
            r.isSuccessful
        }
    }

    override suspend fun storeUnsentFcmToken(token: FcmToken) {
        fcmLocalDataSource.storeUnsentFcmToken(token)
    }

    override suspend fun removeUnsentFcmToken() {
        fcmLocalDataSource.removeUnsentFcmToken()
    }

    override suspend fun sendNotification(recipientId: String, data: Map<String, String>) {
        fcmApi.sendNotification(recipientId, Gson().toJson(data))
    }
}