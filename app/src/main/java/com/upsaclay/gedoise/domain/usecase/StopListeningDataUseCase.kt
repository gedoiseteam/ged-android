package com.upsaclay.gedoise.domain.usecase

import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository

class StopListeningDataUseCase(
    private val userConversationRepository: UserConversationRepository,
    private val messageRepository: MessageRepository
) {
    operator fun invoke() {
        userConversationRepository.stopListenConversations()
        messageRepository.stopListenMessages()
    }
}