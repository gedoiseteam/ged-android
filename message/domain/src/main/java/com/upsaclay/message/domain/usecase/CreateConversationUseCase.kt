package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CreateConversationUseCase(
    private val userConversationRepository: UserConversationRepository,
    private val scope: CoroutineScope
) {
    operator fun invoke(conversation: ConversationUI) {
        val conversationUser = ConversationMapper.toConversationUser(conversation)
        scope.launch {
            userConversationRepository.createConversation(conversationUser)
            userConversationRepository.updateConversation(conversationUser.copy(state = ConversationState.CREATED))
        }
    }
}