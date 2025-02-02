package com.upsaclay.gedoise.domain.usecase

import com.upsaclay.message.domain.repository.UserConversationRepository

class StartListeningDataUseCase(
    private val userConversationRepository: UserConversationRepository
) {
    operator fun invoke() {
        userConversationRepository.listenConversations()
    }
}