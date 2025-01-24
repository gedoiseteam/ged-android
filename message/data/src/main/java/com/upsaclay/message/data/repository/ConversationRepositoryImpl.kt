package com.upsaclay.message.data.repository

import com.upsaclay.common.domain.model.User
import com.upsaclay.message.data.local.ConversationLocalDataSource
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.data.remote.ConversationRemoteDataSource
import com.upsaclay.message.data.remote.model.Conversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ConversationRepositoryImpl(
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val conversationRemoteDataSource: ConversationRemoteDataSource
): ConversationRepository {
    override fun getConversationsFromRemote(userId: String): Flow<List<Conversation>> {
        return conversationRemoteDataSource.listenConversations(userId)
            .map { conversations ->
                conversations.mapNotNull { ConversationMapper.toConversation(it, userId) }
            }
    }

    override fun getConversationFromLocal(): Flow<List<Pair<Conversation, User>>> {
        return conversationLocalDataSource.getConversations()
            .map { conversations ->
                conversations.map(ConversationMapper::toConversationWithInterlocutor)
            }
    }

    override suspend fun createConversation(
        conversation: Conversation,
        interlocutor: User,
        currentUser: User
    ) {
        conversationLocalDataSource.insertConversation(ConversationMapper.toLocal(conversation, interlocutor))
        conversationRemoteDataSource.createConversation(ConversationMapper.toRemote(conversation, currentUser.id))
    }

    override suspend fun upsertLocalConversation(conversation: Conversation, interlocutor: User) {
        conversationLocalDataSource.upsertConversation(ConversationMapper.toLocal(conversation, interlocutor))
    }

    override suspend fun updateLocalConversation(conversation: Conversation, interlocutor: User) {
        conversationLocalDataSource.updateConversation(ConversationMapper.toLocal(conversation, interlocutor))
    }

    override suspend fun deleteConversation(conversation: Conversation, interlocutor: User) {
        conversationLocalDataSource.deleteConversation(ConversationMapper.toLocal(conversation, interlocutor))
        conversationRemoteDataSource.deleteConversation(conversation.id)
    }
}