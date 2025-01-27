package com.upsaclay.message.data.repository

import com.upsaclay.common.domain.entity.User
import com.upsaclay.message.data.remote.model.Conversation
import kotlinx.coroutines.flow.Flow

internal interface ConversationRepository {
    fun getConversationsFromRemote(currentUserId: String): Flow<Conversation>

    fun getConversationFromLocal(): Flow<Pair<Conversation, User>>

    suspend fun createConversation(conversation: Conversation, interlocutor: User, currentUser: User)

    suspend fun upsertLocalConversation(conversation: Conversation, interlocutor: User)

    suspend fun updateLocalConversation(conversation: Conversation, interlocutor: User)

    suspend fun deleteConversation(conversation: Conversation, interlocutor: User)

    suspend fun deleteLocalConversations()
}