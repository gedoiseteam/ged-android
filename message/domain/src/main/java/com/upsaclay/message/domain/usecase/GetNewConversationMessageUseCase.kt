package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.ConversationMessage
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class GetNewConversationMessageUseCase(
    private val conversationRepository: UserConversationRepository,
    private val messageRepository: MessageRepository
) {
    operator fun invoke(): Flow<List<ConversationMessage>> {
        return conversationRepository.getConversations().flatMapLatest { conversations ->
            conversations.asFlow().flatMapLatest { conversation ->
                messageRepository.getRemoteMessages(conversation.id)
                    .map { messages ->
                        messages
                            .filterNot { it.isSeen() }
                            .map { ConversationMapper.toConversationMessage(conversation, it) }
                    }
            }
        }
    }
}