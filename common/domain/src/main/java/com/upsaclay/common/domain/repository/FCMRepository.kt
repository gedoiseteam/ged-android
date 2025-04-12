package com.upsaclay.common.domain.repository

import com.upsaclay.common.domain.entity.FcmToken
import kotlinx.coroutines.flow.Flow

interface FCMRepository {
    val fcmToken: Flow<FcmToken?>

    suspend fun sendFcmToken(token: FcmToken)

    suspend fun storeUnsentFcmToken(token: FcmToken)

    suspend fun removeUnsentFcmToken()

    suspend fun sendNotification(recipientId: String, data: Map<String, String>)
}