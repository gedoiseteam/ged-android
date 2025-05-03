package com.upsaclay.gedoise.domain.usecase

import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.ConversationRepository

class ClearDataUseCase(
    private val userRepository: UserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke() {
        userRepository.deleteCurrentUser()
        conversationRepository.deleteLocalConversations()
        messageRepository.deleteLocalMessages()
    }
}