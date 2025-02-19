package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.repository.UserConversationRepository

class CreateConversationUseCase(
    private val userConversationRepository: UserConversationRepository
) {
    suspend operator fun invoke(conversationUser: ConversationUser) {
        userConversationRepository.createConversation(conversationUser)
        userConversationRepository.updateConversation(conversationUser.copy(state = ConversationState.CREATED))
    }
}