package com.upsaclay.message.domain.repository

import androidx.paging.PagingData
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationMessage
import kotlinx.coroutines.flow.Flow

interface UserConversationRepository {
    val conversations: Flow<List<Conversation>>

    fun getPagedConversationMessages(): Flow<PagingData<ConversationMessage>>

    suspend fun getConversation(interlocutorId: String): Conversation?

    suspend fun createConversation(conversation: Conversation)

    suspend fun updateConversation(conversation: Conversation)

    suspend fun deleteConversation(conversation: Conversation)

    suspend fun deleteLocalConversations()

    suspend fun listenRemoteConversations()
}