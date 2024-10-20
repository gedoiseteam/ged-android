package com.upsaclay.message.data.local

import com.upsaclay.message.data.local.dao.ConversationDao
import com.upsaclay.message.data.local.model.LocalConversation
import kotlinx.coroutines.flow.Flow

internal class ConversationLocalDataSource(
    private val conversationDao: ConversationDao
) {
    fun getAllConversations(): Flow<List<LocalConversation>> = conversationDao.getAllConversationsFlow()

    suspend fun insertConversation(localConversation: LocalConversation) {
        conversationDao.insertConversation(localConversation)
    }

    suspend fun updateConversation(localConversation: LocalConversation) {
        conversationDao.updateConversation(localConversation)
    }

    suspend fun deleteConversation(localConversation: LocalConversation) {
        conversationDao.deleteConversation(localConversation)
    }

    suspend fun setConversationActive(localConversation: LocalConversation) {
        conversationDao.updateConversation(localConversation.copy(isActive = true))
    }
}