package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.model.Conversation
import com.upsaclay.message.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class GetConversationUseCase(private val conversationRepository: ConversationRepository) {
    operator fun invoke(interlocutorId: String): Flow<Conversation> =
        conversationRepository.conversations.map { conversations ->
            conversations.find { it.interlocutor.id == interlocutorId }
        }.filterNotNull()
}