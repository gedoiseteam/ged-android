package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

class GetMessagesUseCase(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(conversationId: String): Flow<Message> =
        messageRepository.getMessages(conversationId)
}