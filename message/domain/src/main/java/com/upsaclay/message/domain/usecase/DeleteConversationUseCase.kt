package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteConversationUseCase(
    private val userConversationRepository: UserConversationRepository,
    private val messageRepository: MessageRepository,
    private val listenConversationsUiUseCase: ListenConversationsUiUseCase,
    private val scope: CoroutineScope
) {
    operator fun invoke(conversation: ConversationUI) {
        scope.launch {
            userConversationRepository.deleteConversation(
                ConversationMapper.toConversationUser(
                    conversation
                )
            )
            listenConversationsUiUseCase.deleteConversation(conversation)
            messageRepository.deleteMessages(conversation.id)
        }
    }
}