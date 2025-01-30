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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class UserConversationRepositoryImpl(
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository,
    private val scope: CoroutineScope
) : UserConversationRepository {
    private val _userConversations = MutableStateFlow<Map<String, ConversationUser>>(mapOf())
    override val userConversations: Flow<ConversationUser> = _userConversations
        .flatMapConcat { conversations ->
            flow { conversations.forEach { emit(it.value) } }
        }
    private val jobs = mutableListOf<Job>()

    private fun listenLocalConversations() {
        val job = scope.launch {
            conversationRepository.getConversationFromLocal().collect { (conversation, interlocutor) ->
                val conversationUser = ConversationMapper.toConversationUser(conversation, interlocutor)
                _userConversations.value += (conversationUser.id to conversationUser)
            }
        }
        jobs.add(job)
    }

    private fun listenRemoteConversations() {
        val job = scope.launch {
            userRepository.currentUser.filterNotNull().collect { currentUser ->
                conversationRepository.getConversationsFromRemote(currentUser.id).collect { conversation ->
                    userRepository.getUserFlow(conversation.interlocutorId).collect { interlocutor ->
                        conversationRepository.upsertLocalConversation(conversation, interlocutor)
                    }
                }
            }
        }
        jobs.add(job)
    }

    override fun getUserConversation(conversationId: String): ConversationUser? =
        _userConversations.value.values.find { it.id == conversationId }

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

    override fun stopListenConversations() {
        jobs.forEach { it.cancel() }
    }

    override fun listenConversations() {
        stopListenConversations()
        listenLocalConversations()
        listenRemoteConversations()
    }
}