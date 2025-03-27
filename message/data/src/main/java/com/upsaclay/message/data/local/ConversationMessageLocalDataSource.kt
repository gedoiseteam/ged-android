package com.upsaclay.message.data.local

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.upsaclay.message.data.local.dao.ConversationMessageDao
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.domain.entity.ConversationMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConversationMessageLocalDataSource(
    private val conversationMessageDao: ConversationMessageDao
) {
    fun getPagedConversationsWithLastMessage(): Flow<PagingData<ConversationMessage>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { conversationMessageDao.getPagedConversationsWithLastMessage() }
        ).flow.map { pagingData ->
            pagingData.map(ConversationMapper::toConversationMessage)
        }
    }

    fun getConversationsWithLastMessage(): Flow<List<ConversationMessage>> {
        return conversationMessageDao.getConversationWithLastMessages().map { messages ->
            messages.map(ConversationMapper::toConversationMessage)
        }
    }
}