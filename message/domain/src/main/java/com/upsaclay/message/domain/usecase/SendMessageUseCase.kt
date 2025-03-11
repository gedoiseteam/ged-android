package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.repository.MessageRepository

class SendMessageUseCase(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(message: Message) {
        messageRepository.createMessage(message)
    }
}