package com.upsaclay.message.data.repository

import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class UserConversationRepositoryImpl(
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository,
) : UserConversationRepository {
    private val _userConversations = MutableStateFlow<Map<String, ConversationUser>>(mapOf())
    override val userConversations: Flow<ConversationUser> = _userConversations
        .flatMapConcat { conversations ->
            flow { conversations.forEach { emit(it.value) } }
        }

    override fun getUserConversation(interlocutorId: String): ConversationUser? =
        _userConversations.value.values.find { it.interlocutor.id == interlocutorId }

    override suspend fun createConversation(conversationUser: ConversationUser) {
        val currentUser = userRepository.currentUser.first() ?: throw Exception()
        conversationRepository.createConversation(
            ConversationMapper.toConversation(conversationUser),
            conversationUser.interlocutor,
            currentUser
        )
    }

    override suspend fun updateConversation(conversationUser: ConversationUser) {
        conversationRepository.upsertLocalConversation(
            ConversationMapper.toConversation(conversationUser),
            conversationUser.interlocutor
        )
    }

    override suspend fun deleteConversation(conversationUser: ConversationUser) {
        conversationRepository.deleteConversation(
            ConversationMapper.toConversation(conversationUser),
            conversationUser.interlocutor
        )
    }

    override suspend fun deleteLocalConversations() {
        _userConversations.value = emptyMap()
        conversationRepository.deleteLocalConversations()
    }

    override suspend fun listenLocalConversations() {
        conversationRepository.getConversationFromLocal()
            .map { (conversation, user) ->
                ConversationMapper.toConversationUser(conversation, user)
            }
            .collect { conversationUser ->
                _userConversations.update { currentMap ->
                    currentMap.toMutableMap().apply { put(conversationUser.id, conversationUser) }
                }
            }
    }

    override suspend fun listenRemoteConversations() {
        userRepository.currentUser.filterNotNull().flatMapConcat { currentUser ->
            conversationRepository.getConversationsFromRemote(currentUser.id).flatMapMerge { conversation ->
                userRepository.getUserFlow(conversation.interlocutorId).map { interlocutor ->
                    Pair(conversation, interlocutor)
                }
            }
        }.collect { (conversation, interlocutor) ->
            conversationRepository.upsertLocalConversation(conversation, interlocutor)
        }
    }
}