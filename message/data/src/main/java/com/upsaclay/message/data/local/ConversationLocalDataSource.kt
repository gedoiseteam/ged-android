package com.upsaclay.message.data.local

import com.upsaclay.message.data.local.dao.ConversationDao
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.domain.entity.Conversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ConversationLocalDataSource(
    private val conversationDao: ConversationDao
) {
    fun getConversations(): Flow<List<Conversation>> {
        return conversationDao.getConversations().map { conversations ->
            conversations.map(ConversationMapper::toConversation)
        }
    }

    suspend fun getConversation(interlocutorId: String): Conversation? =
        conversationDao.getConversation(interlocutorId)?.let(ConversationMapper::toConversation)

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