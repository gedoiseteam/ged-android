package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.model.Conversation
import com.upsaclay.message.domain.repository.ConversationRepository

class DeleteConversationUseCase(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(conversation: Conversation) {
        conversationRepository.deleteConversation(conversation)
    }
}