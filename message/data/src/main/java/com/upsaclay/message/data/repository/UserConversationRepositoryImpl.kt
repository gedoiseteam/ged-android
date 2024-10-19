package com.upsaclay.message.data.repository

import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.data.mapper.MessageMapper
import com.upsaclay.message.data.model.ConversationDTO
import com.upsaclay.message.domain.model.Conversation
import com.upsaclay.message.domain.model.Message
import com.upsaclay.message.domain.repository.ConversationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class UserConversationRepositoryImpl(
    userRepository: UserRepository,
    private val internalConversationRepository: InternalConversationRepository,
    private val internalMessageRepository: InternalMessageRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ConversationRepository {
    private val _conversations = MutableStateFlow<Set<Conversation>>(emptySet())
    override val conversations: Flow<Set<Conversation>> = _conversations
    private val currentUser = userRepository.currentUserFlow.filterNotNull()
    private var previousConversations = emptyList<ConversationDTO>()

    init {
        scope.launch {
            currentUser.collect {
                internalConversationRepository.listenRemoteConversations(it.id)
            }
        }

        scope.launch {
            internalConversationRepository.conversationsDTO.collect { conversationsDTO ->
                val newsConversationsDTO = conversationsDTO - previousConversations.toSet()

                newsConversationsDTO.forEach { conversationDTO ->
                    val messages = internalMessageRepository.getMessages(conversationDTO.conversationId).map(MessageMapper::toDomain)
                    val interlocutor = userRepository.getUser(conversationDTO.participants.first { it != currentUser.first().id })
                    interlocutor?.let {
                        val conversation = ConversationMapper.toDomain(conversationDTO.conversationId, interlocutor, messages)
                        updateConversations(conversation)
                    }
                }

                previousConversations = conversationsDTO
            }

        }
    }

    override suspend fun createConversation(conversation: Conversation) {
        val participantsIds = listOf(currentUser.first().id, conversation.interlocutor.id)
        internalConversationRepository.createConversation(ConversationMapper.toDTO(conversation, participantsIds))
    }

    private fun updateConversations(conversation: Conversation) {
        _conversations.update {
            it.toMutableSet().apply { add(conversation) }
        }
    }
}