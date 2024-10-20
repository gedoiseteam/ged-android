package com.upsaclay.message.domain.usecase

import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.GenerateIDUseCase
import com.upsaclay.message.domain.model.Conversation
import com.upsaclay.message.domain.repository.ConversationRepository

class CreateConversationUseCase(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(interlocutor: User) {
        val conversation = Conversation(
            id = GenerateIDUseCase(),
            interlocutor = interlocutor,
            messages = emptyList()
        )
        conversationRepository.createConversation(conversation)
    }
}