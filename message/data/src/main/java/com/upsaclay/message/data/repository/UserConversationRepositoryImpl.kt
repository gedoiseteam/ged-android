package com.upsaclay.message.data.repository

import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.data.mapper.MessageMapper
import com.upsaclay.message.data.model.ConversationDTO
import com.upsaclay.message.domain.model.Conversation
import com.upsaclay.message.domain.repository.ConversationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

internal class UserConversationRepositoryImpl(
    private val internalConversationRepository: InternalConversationRepository,
    private val internalMessageRepository: InternalMessageRepository,
    private val userRepository: UserRepository,
    private val scope: CoroutineScope = (GlobalScope + Dispatchers.IO)
) : ConversationRepository {
    private val _conversations = MutableStateFlow<Set<Conversation>>(emptySet())
    override val conversations: Flow<Set<Conversation>> = _conversations
    private val currentUser = userRepository.currentUserFlow.filterNotNull()
    private var previousConversations = emptyList<ConversationDTO>()

    init {
        scope.launch {
            currentUser.collect { currentUser ->
                fetchRemoteConversations(currentUser.id)
            }
        }

        scope.launch {
            fetchLocalConversations()
        }
    }

    override suspend fun createConversation(conversation: Conversation) {
        val conversationDTO = ConversationMapper.toDTO(conversation, currentUser.first().id)
        internalConversationRepository.createConversation(conversationDTO)
    }

    private suspend fun fetchRemoteConversations(currentUserId: String) {
        internalConversationRepository.listenRemoteConversations(currentUserId).collect { remoteConversations ->
            remoteConversations.forEach { remoteConversation ->
                val interlocutor = userRepository.getUser(remoteConversation.participants.first { it != currentUserId})
                interlocutor?.let {
                    val conversationDTO = ConversationMapper.toDTO(remoteConversation, interlocutor)
                    val localConversation = ConversationMapper.toLocal(conversationDTO)
                    internalConversationRepository.insertLocalConversation(localConversation)
                }
            }
        }
    }

    private suspend fun fetchLocalConversations() {
        internalConversationRepository.conversationsDTO.collectLatest { conversationsDTO ->
            _conversations.value = conversationsDTO.map { ConversationMapper.toDomain(it, emptyList()) }.toSet()
            val newsConversationsDTO = conversationsDTO - previousConversations.toSet()

            newsConversationsDTO.forEach { conversationDTO ->
                listenConversationMessages(conversationDTO)
            }

            previousConversations = conversationsDTO
        }
    }

    private suspend fun listenConversationMessages(conversationDTO: ConversationDTO) {
        internalMessageRepository.listenLastMessages(conversationDTO.conversationId)
            .map { messagesDTO ->
                messagesDTO.map { MessageMapper.toDomain(it, currentUser.first().id) }
            }
            .collect { messages ->
                val conversation = ConversationMapper.toDomain(conversationDTO, messages)
                updateConversations(conversation)
            }
    }

    private fun updateConversations(conversation: Conversation) {
        _conversations.update {
            it.toMutableSet().apply { add(conversation) }
        }
    }

    override suspend fun setConversationActive(conversation: Conversation) {
        internalConversationRepository.setConversationActive(ConversationMapper.toDTO(conversation, currentUser.first().id))
    }

    override suspend fun deleteConversation(conversation: Conversation) {
        val conversationDTO = ConversationMapper.toDTO(conversation, currentUser.first().id)
        internalConversationRepository.deleteConversation(conversationDTO)
    }
}