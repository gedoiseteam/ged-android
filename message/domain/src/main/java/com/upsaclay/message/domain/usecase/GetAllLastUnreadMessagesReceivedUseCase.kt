package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllLastUnreadMessagesReceivedUseCase(
    private val conversationRepository: UserConversationRepository
) {
    operator fun invoke(currentUserId: String): Flow<List<Message>> {
        return conversationRepository.conversationsWithLastMessage.map { conversationsMessageFixture ->
            conversationsMessageFixture
                .filter { it.lastMessage?.senderId != currentUserId }
                .filter { it.lastMessage?.isSeen() == false }
                .mapNotNull { it.lastMessage }
        }
    }
}