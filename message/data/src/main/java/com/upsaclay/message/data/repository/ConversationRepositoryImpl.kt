package com.upsaclay.message.data.repository

import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.entity.UserNotFoundException
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.message.data.local.ConversationLocalDataSource
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.data.remote.ConversationRemoteDataSource
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.repository.ConversationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
internal class ConversationRepositoryImpl(
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val conversationRemoteDataSource: ConversationRemoteDataSource,
    private val userRepository: UserRepository
) : ConversationRepository {
    override val conversations: Flow<List<Conversation>> = conversationLocalDataSource.getConversations()

    override suspend fun getConversationFromLocal(interlocutorId: String): Conversation? =
        conversationLocalDataSource.getConversation(interlocutorId)

    override suspend fun createConversation(conversation: Conversation) {
        val user = userRepository.currentUser ?: throw UserNotFoundException()
        conversationLocalDataSource.insertConversation(conversation)
        conversationRemoteDataSource.createConversation(conversation, user.id)
    }

    override suspend fun upsertLocalConversation(conversation: Conversation) {
        conversationLocalDataSource.upsertConversation(conversation)
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

    override suspend fun getRemoteConversations(): Flow<Conversation> {
        return userRepository.user
            .filterNotNull()
            .distinctUntilChangedBy { it.id }
            .flatMapLatest { user ->
                conversationRemoteDataSource.listenConversations(user.id)
                    .flatMapMerge { remoteConversation ->
                        val interlocutorId = remoteConversation.participants.first { it != user.id }

                        userRepository.getUserFlow(interlocutorId).map { interlocutor ->
                            ConversationMapper.toConversation(remoteConversation, interlocutor)
                        }
                    }
            }
    }
}