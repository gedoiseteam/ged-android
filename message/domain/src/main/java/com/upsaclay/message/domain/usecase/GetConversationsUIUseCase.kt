package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class GetConversationsUIUseCase(
    private val userConversationRepository: UserConversationRepository,
    private val messageRepository: MessageRepository,
    private val scope: CoroutineScope
) {
    private val _conversationsUI = MutableStateFlow<Map<String, ConversationUI>>(mapOf())

    init {
        listenConversationsUI()
    }

    operator fun invoke(): Flow<List<ConversationUI>> = _conversationsUI.map { conversationMap ->
        conversationMap.values.toList().sortedByDescending {
            it.lastMessage?.date ?: it.createdAt
        }
    }

    private fun listenConversationsUI() {
        userConversationRepository.userConversations.flatMapConcat { conversationUser ->
            messageRepository.getLastMessage(conversationUser.id).map { message ->
                ConversationMapper.toConversationUI(conversationUser, message)
            }
        }.map { conversationUI ->
            _conversationsUI.value += (conversationUI.id to conversationUI)
        }.launchIn(scope)
    }
}