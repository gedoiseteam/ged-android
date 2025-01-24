package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetConversationUseCase(
    private val userConversationRepository: UserConversationRepository
) {
    operator fun invoke(conversationId: String): ConversationUser? =
        userConversationRepository.getConversation(conversationId)
}