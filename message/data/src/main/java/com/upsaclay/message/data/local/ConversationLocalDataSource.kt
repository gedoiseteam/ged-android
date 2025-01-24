package com.upsaclay.message.data.local

import com.upsaclay.message.data.local.dao.ConversationDao
import com.upsaclay.message.data.local.model.LocalConversation
import kotlinx.coroutines.flow.Flow

internal class ConversationLocalDataSource(private val conversationDao: ConversationDao) {
    fun getConversations(): Flow<List<LocalConversation>> = conversationDao.getConversations()

    suspend fun insertConversation(localConversation: LocalConversation) {
        conversationDao.insertConversation(localConversation)
    }

    suspend fun updateConversation(localConversation: LocalConversation) {
        conversationDao.updateConversation(localConversation)
    }

    suspend fun upsertConversation(localConversation: LocalConversation) {
        conversationDao.upsertConversation(localConversation)
    }

    suspend fun deleteConversation(localConversation: LocalConversation) {
        conversationDao.deleteConversation(localConversation)
    }
}