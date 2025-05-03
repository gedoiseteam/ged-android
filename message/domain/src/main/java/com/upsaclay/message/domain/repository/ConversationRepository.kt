package com.upsaclay.message.domain.repository

import com.upsaclay.message.domain.entity.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    val conversations: Flow<List<Conversation>>

    suspend fun getRemoteConversations(): Flow<Conversation>

    suspend fun getConversationFromLocal(interlocutorId: String): Conversation?

    suspend fun createConversation(conversation: Conversation)

    suspend fun upsertLocalConversation(conversation: Conversation)

    suspend fun deleteConversation(conversation: Conversation)

    suspend fun deleteLocalConversations()
}