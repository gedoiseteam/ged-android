package com.upsaclay.gedoise.domain.repository

import com.upsaclay.gedoise.domain.entities.FcmToken
import kotlinx.coroutines.flow.Flow

interface CredentialsRepository {
    val fcmToken: Flow<FcmToken?>

    suspend fun sendFcmToken(token: FcmToken)

    suspend fun storeUnsentFcmToken(token: FcmToken)

    suspend fun removeUnsentFcmToken()
}