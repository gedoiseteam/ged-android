package com.upsaclay.gedoise.domain.usecase

import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import com.upsaclay.message.domain.usecase.ListenConversationsUiUseCase

class ClearDataUseCase(
    private val userRepository: UserRepository,
    private val userConversationRepository: UserConversationRepository,
    private val messageRepository: MessageRepository,
    private val listenConversationsUiUseCase: ListenConversationsUiUseCase
) {
    suspend operator fun invoke() {
        userRepository.deleteCurrentUser()
        userConversationRepository.deleteLocalConversations()
        messageRepository.deleteLocalMessages()
        listenConversationsUiUseCase.clearCache()
    }
}