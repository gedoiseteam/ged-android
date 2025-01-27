package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.repository.UserConversationRepository

class GetConversationUseCase(
    private val userConversationRepository: UserConversationRepository
) {
    operator fun invoke(conversationId: String): ConversationUser? =
        userConversationRepository.getUserConversation(conversationId)
}