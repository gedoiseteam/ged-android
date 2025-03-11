package com.upsaclay.message.data.local

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.upsaclay.message.data.local.dao.ConversationDao
import com.upsaclay.message.data.local.dao.ConversationMessageDao
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ConversationLocalDataSource(
    private val conversationDao: ConversationDao,
    private val conversationMessageDao: ConversationMessageDao
) {
    fun getConversationsUser(): Flow<List<Conversation>> =
        conversationDao.getConversations().map { conversations ->
            conversations.map(ConversationMapper::toConversation)
        }

    suspend fun getConversationUser(interlocutorId: String): Conversation? =
        conversationDao.getConversation(interlocutorId)?.let(ConversationMapper::toConversation)

    fun getConversationsMessage(): Flow<PagingData<ConversationMessage>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { conversationMessageDao.getConversationsWithLastMessage() }
        ).flow.map { pagingData ->
            pagingData.map(ConversationMapper::toConversationMessage)
        }
    }

    suspend fun insertConversation(conversation: Conversation) {
        conversationDao.insertConversation(ConversationMapper.toLocal(conversation))
    }

    suspend fun updateConversation(conversation: Conversation) {
        conversationDao.updateConversation(ConversationMapper.toLocal(conversation))
    }

    suspend fun upsertConversation(conversation: Conversation) {
        conversationDao.upsertConversation(ConversationMapper.toLocal(conversation))
    }

    suspend fun deleteConversation(conversation: Conversation) {
        conversationDao.deleteConversation(ConversationMapper.toLocal(conversation))
    }

    suspend fun deleteConversations() {
        conversationDao.deleteConversations()
    }
}