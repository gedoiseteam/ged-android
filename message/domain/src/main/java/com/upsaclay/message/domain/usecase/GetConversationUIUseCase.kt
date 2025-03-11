package com.upsaclay.message.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetConversationUIUseCase(
    private val userConversationRepository: UserConversationRepository
) {
    operator fun invoke(): Flow<PagingData<ConversationUI>> =
        userConversationRepository.getPagedConversationMessages().map { conversationsMessage ->
            conversationsMessage.map {
                ConversationMapper.toConversationUI(it.conversation, it.lastMessage)
            }
        }
}