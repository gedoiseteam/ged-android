package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.repository.ConversationRepository

class CreateConversationUseCase(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(conversation: Conversation) {
        conversationRepository.createConversation(conversation)
        conversationRepository.upsertLocalConversation(
            conversation.copy(state = ConversationState.CREATED)
        )
    }
}