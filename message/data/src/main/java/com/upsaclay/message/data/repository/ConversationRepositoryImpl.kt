package com.upsaclay.message.data.repository

import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.entity.User
import com.upsaclay.message.data.local.ConversationLocalDataSource
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.data.remote.ConversationRemoteDataSource
import com.upsaclay.message.data.remote.model.Conversation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
internal class ConversationRepositoryImpl(
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val conversationRemoteDataSource: ConversationRemoteDataSource
) : ConversationRepository {
    override fun getConversationsFromRemote(currentUserId: String): Flow<Conversation> =
        conversationRemoteDataSource.listenConversations(currentUserId).mapNotNull {
            ConversationMapper.toConversation(it, currentUserId)
        }

    override fun getConversationFromLocal(): Flow<Pair<Conversation, User>> =
        conversationLocalDataSource.getConversations()
            .flatMapConcat { conversations ->
                flow {
                    conversations.forEach {
                        emit(ConversationMapper.toConversationWithInterlocutor(it))
                    }
                }
            }

    override suspend fun createConversation(
        conversation: Conversation,
        interlocutor: User,
        currentUser: User
    ) {
        conversationLocalDataSource.insertConversation(
            ConversationMapper.toLocal(conversation, interlocutor)
        )
        conversationRemoteDataSource.createConversation(
            ConversationMapper.toRemote(conversation, currentUser.id)
        )
    }

    override suspend fun upsertLocalConversation(conversation: Conversation, interlocutor: User) {
        conversationLocalDataSource.upsertConversation(
            ConversationMapper.toLocal(conversation, interlocutor)
        )
    }

    override suspend fun updateLocalConversation(conversation: Conversation, interlocutor: User) {
        conversationLocalDataSource.updateConversation(
            ConversationMapper.toLocal(conversation, interlocutor)
        )
    }

    override suspend fun deleteConversation(conversation: Conversation, interlocutor: User) {
        try {
            conversationRemoteDataSource.deleteConversation(conversation.id)
            conversationLocalDataSource.deleteConversation(ConversationMapper.toLocal(conversation, interlocutor))
        } catch (e: Exception) {
            e("Error deleting conversation", e)
            throw IOException()
        }
    }

    override suspend fun deleteLocalConversations() {
        conversationLocalDataSource.deleteConversations()
    }
}