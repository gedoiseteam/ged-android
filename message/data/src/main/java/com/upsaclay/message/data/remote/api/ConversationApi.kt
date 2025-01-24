package com.upsaclay.message.data.remote.api

import com.upsaclay.message.data.remote.model.RemoteConversation
import kotlinx.coroutines.flow.Flow

internal interface ConversationApi {
    fun listenConversations(userId: String): Flow<List<RemoteConversation>>

    suspend fun createConversation(remoteConversation: RemoteConversation)

    suspend fun deleteConversation(conversationId: String)
}