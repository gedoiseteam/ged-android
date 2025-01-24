package com.upsaclay.message.domain.usecase

import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.GenerateIDUseCase
import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.repository.UserConversationRepository
import java.time.LocalDateTime

class CreateConversationUseCase(
    private val userConversationRepository: UserConversationRepository
) {
    suspend operator fun invoke(conversationUI: ConversationUI) {
        val conversationUser = ConversationMapper.toConversationUser(conversationUI)
        userConversationRepository.createConversation(conversationUser)
        userConversationRepository.updateConversation(conversationUser.copy(state = ConversationState.CREATED))
    }
}