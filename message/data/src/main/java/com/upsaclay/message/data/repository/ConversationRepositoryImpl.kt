package com.upsaclay.message.data.repository

import androidx.paging.PagingData
import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.entity.User
import com.upsaclay.message.data.local.ConversationLocalDataSource
import com.upsaclay.message.data.remote.ConversationRemoteDataSource
import com.upsaclay.message.data.remote.model.RemoteConversation
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationMessage
import kotlinx.coroutines.flow.Flow
import java.io.IOException

internal class ConversationRepositoryImpl(
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val conversationRemoteDataSource: ConversationRemoteDataSource
): ConversationRepository {
    override fun getConversationUser(): Flow<List<Conversation>> =
        conversationLocalDataSource.getConversationsUser()

    override fun getPagedConversationMessages(): Flow<PagingData<ConversationMessage>> =
        conversationLocalDataSource.getConversationsMessage()

    override suspend fun getConversationFromLocal(interlocutorId: String): Conversation? =
        conversationLocalDataSource.getConversationUser(interlocutorId)

    override fun getConversationsFromRemote(currentUserId: String): Flow<RemoteConversation> =
        conversationRemoteDataSource.listenConversations(currentUserId)

    override suspend fun createConversation(conversation: Conversation, currentUser: User) {
        conversationLocalDataSource.insertConversation(conversation)
        conversationRemoteDataSource.createConversation(conversation, currentUser.id)
    }

    override suspend fun upsertLocalConversation(conversation: Conversation) {
        conversationLocalDataSource.upsertConversation(conversation)
    }

    override suspend fun updateLocalConversation(conversation: Conversation) {
        conversationLocalDataSource.updateConversation(conversation)
    }

    override suspend fun deleteConversation(conversation: Conversation) {
        try {
            conversationRemoteDataSource.deleteConversation(conversation.id)
            conversationLocalDataSource.deleteConversation(conversation)
        } catch (e: Exception) {
            e("Error deleting conversation", e)
            throw IOException()
        }
    }

    override suspend fun deleteLocalConversations() {
        conversationLocalDataSource.deleteConversations()
    }
}