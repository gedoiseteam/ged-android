package com.upsaclay.message.domain.repository

import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationMessage
import kotlinx.coroutines.flow.Flow

interface UserConversationRepository {
    val conversationsMessage: Flow<List<ConversationMessage>>

    val conversations: Flow<List<Conversation>>

    suspend fun listenRemoteConversations()

    suspend fun getConversationFromLocal(interlocutorId: String): Conversation?

    suspend fun createConversation(conversation: Conversation)

    suspend fun updateConversation(conversation: Conversation)

    suspend fun deleteConversation(conversation: Conversation)

    suspend fun deleteLocalConversations()
}