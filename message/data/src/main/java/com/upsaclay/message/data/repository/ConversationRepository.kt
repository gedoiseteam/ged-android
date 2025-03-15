package com.upsaclay.message.data.repository

import com.upsaclay.common.domain.entity.User
import com.upsaclay.message.data.remote.model.RemoteConversation
import com.upsaclay.message.domain.entity.Conversation
import kotlinx.coroutines.flow.Flow

internal interface ConversationRepository {
    fun getConversations(): Flow<List<Conversation>>

    suspend fun getConversationFromLocal(interlocutorId: String): Conversation?

    fun getConversationsFromRemote(currentUserId: String): Flow<RemoteConversation>

    suspend fun createConversation(conversation: Conversation, currentUser: User)

    suspend fun upsertLocalConversation(conversation: Conversation)

    suspend fun updateLocalConversation(conversation: Conversation)

    suspend fun deleteConversation(conversation: Conversation)

    suspend fun deleteLocalConversations()
}