package com.upsaclay.message.domain.repository

import com.upsaclay.message.domain.entity.ConversationMessage
import kotlinx.coroutines.flow.Flow

interface ConversationMessageRepository {
    val conversationsMessage: Flow<List<ConversationMessage>>
}