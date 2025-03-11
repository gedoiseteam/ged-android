package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.repository.MessageRepository

class GetLastMessageUseCase(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(conversationId: Int) = messageRepository.getLastMessage(conversationId)
}