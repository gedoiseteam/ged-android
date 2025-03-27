package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.repository.UserConversationRepository

class CreateConversationUseCase(
    private val userConversationRepository: UserConversationRepository
) {
    suspend operator fun invoke(conversation: Conversation) {
        userConversationRepository.createConversation(conversation)
        userConversationRepository.updateConversation(conversation.copy(state = ConversationState.CREATED))
    }
}