package com.upsaclay.message.data.local

import com.upsaclay.message.data.local.dao.MessageDao
import com.upsaclay.message.data.mapper.MessageMapper
import com.upsaclay.message.domain.entity.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
internal class MessageLocalDataSource(private val messageDao: MessageDao) {
    fun getMessages(conversationId: String, offset: Int = 0): Flow<Message> =
        messageDao.getMessages(conversationId, offset).flatMapConcat { messages ->
            flow {
                messages.forEach { emit(MessageMapper.toDomain(it)) }
            }
        }

    fun getLastMessage(conversationId: String): Flow<Message?> =
        messageDao.getLastMessage(conversationId).map {
            it?.let { MessageMapper.toDomain(it) }
        }

    suspend fun insertMessage(message: Message) {
        messageDao.insertMessage(MessageMapper.toLocal(message))
    }

    suspend fun updateMessage(message: Message) {
        messageDao.updateMessage(MessageMapper.toLocal(message))
    }

    suspend fun upsertMessage(message: Message) {
        messageDao.upsertMessage(MessageMapper.toLocal(message))
    }
}