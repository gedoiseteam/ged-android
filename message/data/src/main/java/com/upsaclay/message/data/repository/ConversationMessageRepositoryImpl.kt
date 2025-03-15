package com.upsaclay.message.data.repository

import androidx.paging.PagingData
import com.upsaclay.message.data.local.ConversationMessageLocalDataSource
import com.upsaclay.message.domain.entity.ConversationMessage
import kotlinx.coroutines.flow.Flow

class ConversationMessageRepositoryImpl(
    private val conversationMessageLocalDataSource: ConversationMessageLocalDataSource
): ConversationMessageRepository {
    override fun getPagedConversationsWithLastMessage(): Flow<PagingData<ConversationMessage>> =
        conversationMessageLocalDataSource.getPagedConversationsWithLastMessage()

    override fun getConversationsWithLastMessage(): Flow<List<ConversationMessage>> =
        conversationMessageLocalDataSource.getConversationsWithLastMessage()
}