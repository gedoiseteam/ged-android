package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.repository.UserConversationRepository

class CreateConversationUseCase(
    private val userConversationRepository: UserConversationRepository
) {
    suspend operator fun invoke(conversation: ConversationUI) {
        val conversationUser = ConversationMapper.toConversationUser(conversation)
        userConversationRepository.createConversation(conversationUser)
        userConversationRepository.updateConversation(conversationUser.copy(state = ConversationState.CREATED))
    }
}