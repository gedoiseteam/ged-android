package com.upsaclay.message.domain.usecase

import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.GenerateIDUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.message.domain.model.Conversation
import com.upsaclay.message.domain.model.Message
import com.upsaclay.message.domain.repository.ConversationRepository
import com.upsaclay.message.domain.repository.MessageRepository

class SendMessageUseCase(
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend operator fun invoke(conversation: Conversation, message: Message) {
        getCurrentUserUseCase()?.let { currentUser ->
            messageRepository.sendMessage(conversation.id, message.copy(id = GenerateIDUseCase()), currentUser.id)
            if (conversation.messages.isEmpty()) {
                conversationRepository.setConversationActive(conversation)
            }
        }
    }
}