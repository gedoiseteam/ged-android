package com.upsaclay.message.data.local

import com.upsaclay.message.data.local.dao.MessageDao
import com.upsaclay.message.data.mapper.MessageMapper
import com.upsaclay.message.domain.entity.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext


internal class MessageLocalDataSource(private val messageDao: MessageDao) {
    fun getMessages(conversationId: Int): Flow<List<Message>> =
        messageDao.getMessages(conversationId).map { messages ->
            messages.map(MessageMapper::toDomain)
        }

    fun getUnreadMessages(conversationId: Int): Flow<List<Message>> =
        messageDao.getUnreadMessages(conversationId).map { messages ->
            messages.map(MessageMapper::toDomain)
        }

    suspend fun insertMessage(message: Message) {
        withContext(Dispatchers.IO) {
            messageDao.insertMessage(MessageMapper.toLocal(message))
        }
    }

    suspend fun updateMessage(message: Message) {
        withContext(Dispatchers.IO) {
            messageDao.updateMessage(MessageMapper.toLocal(message))
        }
    }

    suspend fun upsertMessage(message: Message) {
        withContext(Dispatchers.IO) {
            messageDao.upsertMessage(MessageMapper.toLocal(message))
        }
    }

    suspend fun deleteMessages(conversationId: Int) {
        withContext(Dispatchers.IO) {
            messageDao.deleteMessages(conversationId)
        }
    }

    suspend fun deleteMessages() {
        withContext(Dispatchers.IO) {
            messageDao.deleteAllMessages()
        }
    }
}