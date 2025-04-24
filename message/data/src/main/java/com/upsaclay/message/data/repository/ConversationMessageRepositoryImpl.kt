package com.upsaclay.message.data.repository

import com.upsaclay.message.data.local.ConversationMessageLocalDataSource
import com.upsaclay.message.domain.entity.ConversationMessage
import kotlinx.coroutines.flow.Flow

class ConversationMessageRepositoryImpl(
    private val conversationMessageLocalDataSource: ConversationMessageLocalDataSource
): ConversationMessageRepository {
    override fun getConversationsMessage(): Flow<List<ConversationMessage>> =
        conversationMessageLocalDataSource.getConversationsMessage()
}