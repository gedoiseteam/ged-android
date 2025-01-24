package com.upsaclay.message.data.repository

import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

@OptIn(ExperimentalCoroutinesApi::class)
internal class UserConversationRepositoryImpl(
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository,
    private val scope: CoroutineScope = (GlobalScope + Dispatchers.IO)
): UserConversationRepository  {
    private val _userConversations = MutableStateFlow<List<ConversationUser>>(emptyList())
    override val userConversations: Flow<ConversationUser> = _userConversations
        .flatMapConcat { conversations ->
            flow { conversations.forEach { emit(it) } }
        }

    init {
        listenLocalConversations()
        listenRemoteConversations()
    }

    override fun getConversation(conversationId: String): ConversationUser? =
        _userConversations.value.find { it.id == conversationId }

    override suspend fun createConversation(conversationUser: ConversationUser) {
        val conversation = ConversationMapper.toConversation(conversationUser)
        val currentUser = userRepository.currentUser.first() ?: throw Exception()
        conversationRepository.createConversation(conversation, conversationUser.interlocutor, currentUser)
    }

    override suspend fun updateConversation(conversationUser: ConversationUser) {
        val conversation = ConversationMapper.toConversation(conversationUser)
        conversationRepository.upsertLocalConversation(conversation, conversationUser.interlocutor)
    }

    override suspend fun deleteConversation(conversationUser: ConversationUser) {
        val conversation = ConversationMapper.toConversation(conversationUser)
        conversationRepository.deleteConversation(conversation, conversationUser.interlocutor)
    }

    private fun listenLocalConversations() {
        scope.launch {
            conversationRepository.getConversationFromLocal().collect { conversationsInterlocutor ->
                _userConversations.value = conversationsInterlocutor.map { ConversationMapper.toConversationUser(it.first, it.second) }
            }
        }
    }

    private fun listenRemoteConversations() {
        scope.launch {
            val user = userRepository.currentUser.first() ?: return@launch
            conversationRepository.getConversationsFromRemote(user.id).collect { conversations ->
                conversations.forEach {
                    conversationRepository.upsertLocalConversation(it, user)
                }
            }
        }
    }
}