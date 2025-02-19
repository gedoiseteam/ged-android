package com.upsaclay.message.domain.usecase

import com.upsaclay.common.domain.entity.User
import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.flow.firstOrNull

class GetConversationUserUseCase(
    private val userConversationRepository: UserConversationRepository
) {
    operator fun invoke(interlocutorId: String): ConversationUser? =
        userConversationRepository.getUserConversation(interlocutorId)
}