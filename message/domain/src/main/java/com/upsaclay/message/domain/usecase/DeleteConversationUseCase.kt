package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.ConversationRepository

class DeleteConversationUseCase(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(conversation: Conversation) {
        conversationRepository.deleteConversation(conversation)
        messageRepository.deleteMessages(conversation.id)
    }
}