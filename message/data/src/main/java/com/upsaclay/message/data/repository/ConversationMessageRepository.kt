package com.upsaclay.message.data.repository

import androidx.paging.PagingData
import com.upsaclay.message.domain.entity.ConversationMessage
import kotlinx.coroutines.flow.Flow

interface ConversationMessageRepository {
    fun getPagedConversationsWithLastMessage(): Flow<PagingData<ConversationMessage>>

    fun getConversationsWithLastMessage(): Flow<List<ConversationMessage>>
}