package com.upsaclay.message.data.repository

import com.upsaclay.message.data.local.model.LocalConversation
import com.upsaclay.message.data.model.ConversationDTO
import com.upsaclay.message.data.remote.model.RemoteConversation
import kotlinx.coroutines.flow.Flow

internal interface InternalConversationRepository {
    val conversationsDTO: Flow<List<ConversationDTO>>

    suspend fun listenRemoteConversations(userId: Int): Flow<List<RemoteConversation>>

    suspend fun insertLocalConversation(localConversation: LocalConversation)

    suspend fun createConversation(conversationDTO: ConversationDTO)

    suspend fun setConversationActive(conversationDTO: ConversationDTO)

    suspend fun deleteConversation(conversationDTO: ConversationDTO)
}