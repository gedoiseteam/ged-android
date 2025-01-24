package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import com.upsaclay.message.domain.repository.MessageRepository

class SendMessageUseCase(private val messageRepository: MessageRepository) {
    suspend operator fun invoke(message: Message) {
        try {
            messageRepository.createMessage(message)
            messageRepository.updateMessage(message.copy(state = MessageState.SENT))
        } catch (e: Exception) {
            messageRepository.upsertMessage(message.copy(state = MessageState.ERROR))
        }
    }
}