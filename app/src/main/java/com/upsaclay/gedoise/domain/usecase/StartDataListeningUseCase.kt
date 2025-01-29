package com.upsaclay.gedoise.domain.usecase

import com.upsaclay.message.domain.repository.UserConversationRepository

class StartDataListeningUseCase(
    private val userConversationRepository: UserConversationRepository
) {
    operator fun invoke() {
        userConversationRepository.listenConversations()
    }
}