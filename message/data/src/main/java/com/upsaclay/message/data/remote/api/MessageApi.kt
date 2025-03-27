package com.upsaclay.message.data.remote.api

import com.upsaclay.message.data.remote.model.RemoteMessage
import kotlinx.coroutines.flow.Flow

internal interface MessageApi {
    fun listenMessages(conversationId: Int): Flow<List<RemoteMessage>>

    suspend fun createMessage(remoteMessage: RemoteMessage)

    suspend fun updateSeenMessage(remoteMessage: RemoteMessage)

    suspend fun deleteMessages(conversationId: Int)
}