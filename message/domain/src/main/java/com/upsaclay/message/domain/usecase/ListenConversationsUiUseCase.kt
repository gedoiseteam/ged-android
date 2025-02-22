package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.cancel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan

@OptIn(ExperimentalCoroutinesApi::class)
class ListenConversationsUiUseCase(
    private val userConversationRepository: UserConversationRepository,
    private val messageRepository: MessageRepository,
    private val scope: CoroutineScope
) {
    private val conversationsUIMap = mutableMapOf<String, ConversationUI>()
    private val _conversationsUI = MutableStateFlow<List<ConversationUI>>(emptyList())
    val conversationsUI: Flow<List<ConversationUI>> = _conversationsUI
    val currentConversationsUI: List<ConversationUI>
        get() = _conversationsUI.value
    internal var job: Job? = null

    fun start() {
        job?.cancel()
        listenConversationsUI()
    }

    fun stop() {
        job?.cancel()
    }

    fun clearCache() {
        conversationsUIMap.clear()
        _conversationsUI.value = emptyList()
    }

    fun deleteConversation(conversation: ConversationUI) {
       _conversationsUI.value = _conversationsUI.value.filterNot { it.id == conversation.id }
        conversationsUIMap.remove(conversation.id)
    }

    private fun listenConversationsUI() {
        job = userConversationRepository.userConversations
            .flatMapConcat { conversationUser ->
                messageRepository.getLastMessage(conversationUser.id).map { message ->
                    ConversationMapper.toConversationUI(conversationUser, message)
                }
            }
            .scan(conversationsUIMap) { acc, conversationUI ->
                acc[conversationUI.id] = conversationUI
                acc
            }
            .map { conversationMap ->
                conversationMap.values.sortedByDescending { it.lastMessage?.date ?: it.createdAt }
            }
            .onEach { conversations ->
                _conversationsUI.value = conversations
            }.launchIn(scope)
    }
}