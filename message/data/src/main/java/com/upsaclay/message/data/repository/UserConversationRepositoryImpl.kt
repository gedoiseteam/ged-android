package com.upsaclay.message.data.repository

import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.entity.UserNotFoundException
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationMessage
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
internal class UserConversationRepositoryImpl(
    private val conversationRepository: ConversationRepository,
    private val conversationMessageRepository: ConversationMessageRepository,
    private val userRepository: UserRepository
) : UserConversationRepository {
    override val conversationsMessage: Flow<List<ConversationMessage>> =
        conversationMessageRepository.getConversationsMessage()

    override val conversations: Flow<List<Conversation>> = conversationRepository.getConversations()

    override suspend fun getConversationFromLocal(interlocutorId: String): Conversation? =
        conversationRepository.getConversationFromLocal(interlocutorId)

    override suspend fun createConversation(conversation: Conversation) {
        val currentUser = userRepository.currentUser.first() ?: throw UserNotFoundException()
        conversationRepository.createConversation(conversation, currentUser)
    }

    override suspend fun updateConversation(conversation: Conversation) {
        conversationRepository.upsertLocalConversation(conversation)
    }

    override suspend fun deleteConversation(conversation: Conversation) {
        conversationRepository.deleteConversation(conversation)
    }

    override suspend fun deleteLocalConversations() {
        conversationRepository.deleteLocalConversations()
    }

    override suspend fun listenRemoteConversations() {
        userRepository.currentUser.filterNotNull().collectLatest { currentUser ->
            conversationRepository.getConversationsFromRemote(currentUser.id)
                .flatMapMerge { remoteConversation ->
                    val interlocutorId = remoteConversation.participants.first { it != currentUser.id }

                    userRepository.getUserFlow(interlocutorId).map { interlocutor ->
                        ConversationMapper.toConversation(remoteConversation, interlocutor)
                    }
                }
                .catch { e("Error listen remote conversation", it) }
                .filterNot { conversation ->
                    conversations.first().any { conversation == it }
                }
                .collect {
                    conversationRepository.upsertLocalConversation(it)
                }
        }
    }
}