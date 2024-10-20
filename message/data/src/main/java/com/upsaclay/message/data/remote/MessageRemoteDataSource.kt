package com.upsaclay.message.data.remote

import com.upsaclay.message.data.remote.api.MessageApi
import com.upsaclay.message.data.remote.model.RemoteMessage
import kotlinx.coroutines.flow.Flow

internal class MessageRemoteDataSource(private val messageApi: MessageApi) {
    fun listenLastMessages(conversationId: String): Flow<List<RemoteMessage>> =
        messageApi.listenLastMessages(conversationId)

    suspend fun getMessages(conversationId: String, limit: Long): List<RemoteMessage> =
        messageApi.getMessages(conversationId, limit)

    suspend fun addMessage(conversationId: String, remoteMessage: RemoteMessage): Result<Unit> {
        return messageApi.addMessage(conversationId, remoteMessage)
    }
}