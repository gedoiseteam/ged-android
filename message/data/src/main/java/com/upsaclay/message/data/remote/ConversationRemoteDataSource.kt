package com.upsaclay.message.data.remote

import com.upsaclay.message.data.remote.api.ConversationApi
import com.upsaclay.message.data.remote.model.RemoteConversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class ConversationRemoteDataSource(private val conversationApi: ConversationApi) {
    fun listenConversations(userId: String): Flow<RemoteConversation> =
        conversationApi.listenConversations(userId)

    suspend fun createConversation(remoteConversation: RemoteConversation) {
        withContext(Dispatchers.IO) {
            conversationApi.createConversation(remoteConversation)
        }
    }

    suspend fun deleteConversation(conversationId: String) {
        withContext(Dispatchers.IO) {
            conversationApi.deleteConversation(conversationId)
        }
    }
}