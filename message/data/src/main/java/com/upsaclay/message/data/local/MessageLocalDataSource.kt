package com.upsaclay.message.data.local

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.upsaclay.message.data.local.dao.MessageDao
import com.upsaclay.message.data.mapper.MessageMapper
import com.upsaclay.message.domain.entity.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

private const val MESSAGE_LIMIT = 20

internal class MessageLocalDataSource(private val messageDao: MessageDao) {
    fun getMessages(conversationId: Int): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(
                pageSize = MESSAGE_LIMIT,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { messageDao.getPagedMessages(conversationId) }
        ).flow.map { it.map(MessageMapper::toDomain) }
    }

    fun getLastMessage(conversationId: Int): Flow<Message> =
        messageDao.getLastMessage(conversationId).mapNotNull { localMessage ->
            localMessage?.let { MessageMapper.toDomain(it) }
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