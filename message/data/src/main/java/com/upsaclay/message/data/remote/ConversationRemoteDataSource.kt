package com.upsaclay.message.data.remote

import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.data.remote.api.ConversationApi
import com.upsaclay.message.data.remote.model.RemoteConversation
import com.upsaclay.message.domain.entity.Conversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class ConversationRemoteDataSource(private val conversationApi: ConversationApi) {
    fun listenConversations(userId: String): Flow<RemoteConversation> =
        conversationApi.listenConversations(userId)

    suspend fun createConversation(conversation: Conversation, currentUserId: String) {
        withContext(Dispatchers.IO) {
            conversationApi.createConversation(ConversationMapper.toRemote(conversation, currentUserId))
        }
    }

    suspend fun deleteConversation(conversationId: Int) {
        withContext(Dispatchers.IO) {
            conversationApi.deleteConversation(conversationId)
        }
    }
}