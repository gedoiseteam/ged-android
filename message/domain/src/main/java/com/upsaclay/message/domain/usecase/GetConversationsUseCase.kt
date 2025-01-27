package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

class GetConversationsUseCase(
    private val userConversationRepository: UserConversationRepository,
    private val messageRepository: MessageRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<ConversationUI> =
        userConversationRepository.userConversations.flatMapConcat { conversationUser ->
            messageRepository.getLastMessage(conversationUser.id).map { message ->
                ConversationMapper.toConversationUI(conversationUser, message)
            }
    }
}