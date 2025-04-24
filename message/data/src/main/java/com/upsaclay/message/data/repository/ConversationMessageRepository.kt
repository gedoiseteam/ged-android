package com.upsaclay.message.data.repository

import com.upsaclay.message.domain.entity.ConversationMessage
import kotlinx.coroutines.flow.Flow

interface ConversationMessageRepository {
    fun getConversationsMessage(): Flow<List<ConversationMessage>>
}