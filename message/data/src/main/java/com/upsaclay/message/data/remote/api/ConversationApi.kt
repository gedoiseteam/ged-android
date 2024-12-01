package com.upsaclay.message.data.remote.api

import com.upsaclay.message.data.remote.model.RemoteConversation
import kotlinx.coroutines.flow.Flow

internal interface ConversationApi {
    fun listenAllConversations(userId: String): Flow<List<RemoteConversation>>

    suspend fun createConversation(remoteConversation: RemoteConversation)

    suspend fun deleteConversation(conversationId: String)

    suspend fun setConversationActive(conversationId: String)
}