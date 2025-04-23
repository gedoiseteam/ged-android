package com.upsaclay.common.data.repository

import com.upsaclay.common.data.formatHttpError
import com.upsaclay.common.data.local.FCMLocalDataSource
import com.upsaclay.common.data.remote.api.FCMApi
import com.upsaclay.common.domain.d
import com.upsaclay.common.domain.entity.FcmToken
import com.upsaclay.common.domain.repository.CredentialsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber.Forest.e
import java.io.IOException

class CredentialsRepositoryImpl(
    private val fcmLocalDataSource: FCMLocalDataSource,
    private val fcmApi: FCMApi
): CredentialsRepository {
    override suspend fun getUnsentFcmToken(): FcmToken? = fcmLocalDataSource.getUnsentFcmToken()

    override suspend fun sendFcmToken(token: FcmToken) {
        withContext(Dispatchers.IO) {
            val response = token.userId?.let {
                fcmApi.addToken(it, token.value)
            } ?: run {
                e("User ID is null, cannot send FCM token")
                throw IllegalArgumentException("User ID is null")
            }

            if (!response.isSuccessful) {
                val errorMessage = formatHttpError("Error send fcm token", response)
                e(errorMessage)
                throw IOException(errorMessage)
            } else {
                d(response.body()?.message ?: "Token sent successfully")
            }
        }
    }

    override suspend fun storeUnsentFcmToken(token: FcmToken) {
        fcmLocalDataSource.storeUnsentFcmToken(token)
    }

    override suspend fun removeUnsentFcmToken() {
        fcmLocalDataSource.removeUnsentFcmToken()
    }
}