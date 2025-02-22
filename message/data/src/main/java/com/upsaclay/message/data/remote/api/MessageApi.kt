package com.upsaclay.message.data.remote.api

import com.upsaclay.message.data.remote.model.RemoteMessage
import kotlinx.coroutines.flow.Flow

internal interface MessageApi {
    fun listenMessages(conversationId: String): Flow<RemoteMessage>

    suspend fun createMessage(remoteMessage: RemoteMessage)

    suspend fun updateMessage(remoteMessage: RemoteMessage)

    suspend fun deleteMessages(conversationId: String)
}