package com.upsaclay.message.data.repository

import androidx.paging.PagingData
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationMessage
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
internal class UserConversationRepositoryImpl(
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository
) : UserConversationRepository {
    override val conversations: Flow<List<Conversation>> = conversationRepository.getConversationUser()

    override fun getPagedConversationMessages(): Flow<PagingData<ConversationMessage>> =
        conversationRepository.getPagedConversationMessages()

    override suspend fun getConversation(interlocutorId: String): Conversation? =
        conversationRepository.getConversationFromLocal(interlocutorId)

    override suspend fun createConversation(conversation: Conversation) {
        val currentUser = userRepository.currentUser.first() ?: throw Exception()
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
        userRepository.currentUser.filterNotNull().flatMapConcat { currentUser ->
            conversationRepository.getConversationsFromRemote(currentUser.id)
                .flatMapLatest { remoteConversation ->
                    val interlocutorId =
                        remoteConversation.participants.first { it != currentUser.id }
                    userRepository.getUserFlow(interlocutorId).map { interlocutor ->
                        ConversationMapper.toConversation(remoteConversation, interlocutor)
                    }
                }
        }.collect {
            conversationRepository.upsertLocalConversation(it)
        }
    }
}