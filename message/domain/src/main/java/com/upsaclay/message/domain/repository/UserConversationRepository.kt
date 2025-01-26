package com.upsaclay.message.domain.repository

import com.upsaclay.message.domain.entity.ConversationUser
import kotlinx.coroutines.flow.Flow

interface UserConversationRepository {
    val userConversations: Flow<ConversationUser>

    fun getUserConversation(conversationId: String): ConversationUser?

    suspend fun createConversation(conversationUser: ConversationUser)

    suspend fun updateConversation(conversationUser: ConversationUser)

    suspend fun deleteConversation(conversationUser: ConversationUser)
}