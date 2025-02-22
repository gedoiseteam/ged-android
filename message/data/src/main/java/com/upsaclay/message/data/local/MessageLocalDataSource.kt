package com.upsaclay.message.data.local

import com.upsaclay.message.data.local.dao.MessageDao
import com.upsaclay.message.data.mapper.MessageMapper
import com.upsaclay.message.domain.entity.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
internal class MessageLocalDataSource(private val messageDao: MessageDao) {
    fun getMessages(conversationId: String): Flow<Message> =
        messageDao.getMessages(conversationId).flatMapConcat { messages ->
            flow { messages.forEach { emit(MessageMapper.toDomain(it)) } }
        }

    fun getLastMessage(conversationId: String): Flow<Message?> =
        messageDao.getLastMessage(conversationId).map {
            it?.let { MessageMapper.toDomain(it) }
        }

    suspend fun getMessages(conversationId: String, limit: Int, offset: Int): List<Message> =
        withContext(Dispatchers.IO) {
            messageDao.getMessages(conversationId, limit, offset).map(MessageMapper::toDomain)
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

    suspend fun deleteMessages(conversationId: String) {
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