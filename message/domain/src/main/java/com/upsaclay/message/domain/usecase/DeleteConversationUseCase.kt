package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.repository.UserConversationRepository

class DeleteConversationUseCase(
    private val userConversationRepository: UserConversationRepository
) {
    suspend operator fun invoke(conversationUI: ConversationUI) {
        userConversationRepository.deleteConversation(ConversationMapper.toConversationUser(conversationUI))
    }
}