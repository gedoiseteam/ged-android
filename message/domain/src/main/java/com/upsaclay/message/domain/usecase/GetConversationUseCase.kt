package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.repository.UserConversationRepository

class GetConversationUseCase(
    private val userConversationRepository: UserConversationRepository
) {
    suspend operator fun invoke(interlocutorId: String) = userConversationRepository.getConversation(interlocutorId)
}