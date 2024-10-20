package com.upsaclay.message.data.local

import com.upsaclay.message.data.local.dao.MessageDao
import com.upsaclay.message.data.local.model.LocalMessage
import kotlinx.coroutines.flow.Flow

internal class MessageLocalDataSource(private val messageDao: MessageDao) {
    fun getLastMessages(conversationId: String): Flow<List<LocalMessage>> =
        messageDao.getLastMessages(conversationId)

    suspend fun getMessages(conversationId: String, offset: Int = 0): List<LocalMessage> =
        messageDao.getMessages(conversationId, offset)

    suspend fun insertMessage(localMessage: LocalMessage) {
        messageDao.insertMessage(localMessage)
    }

    suspend fun updateMessage(localMessage: LocalMessage) {
        messageDao.updateMessage(localMessage)
    }
}