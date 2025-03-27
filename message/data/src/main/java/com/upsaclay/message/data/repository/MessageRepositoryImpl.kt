package com.upsaclay.message.data.repository

import androidx.paging.PagingData
import com.upsaclay.message.data.local.MessageLocalDataSource
import com.upsaclay.message.data.remote.MessageRemoteDataSource
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

internal class MessageRepositoryImpl(
    private val messageLocalDataSource: MessageLocalDataSource,
    private val messageRemoteDataSource: MessageRemoteDataSource
): MessageRepository {
    override fun getMessages(conversationId: Int): Flow<PagingData<Message>> =
        messageLocalDataSource.getMessages(conversationId)

    override fun getLastMessage(conversationId: Int): Flow<Message> =
        messageLocalDataSource.getLastMessage(conversationId)

    override fun getUnreadMessages(conversationId: Int): Flow<List<Message>> =
        messageLocalDataSource.getUnreadMessages(conversationId)

    override fun getRemoteMessages(conversationId: Int): Flow<List<Message>> =
        messageRemoteDataSource.listenMessages(conversationId)

    override suspend fun createMessage(message: Message) {
        messageLocalDataSource.insertMessage(message)
        messageRemoteDataSource.createMessage(message)
    }

    override suspend fun updateMessage(message: Message) {
        messageLocalDataSource.updateMessage(message)
        messageRemoteDataSource.updateMessage(message)
    }

    override suspend fun upsertMessage(message: Message) {
        messageLocalDataSource.upsertMessage(message)
    }

    override suspend fun deleteMessages(conversationId: Int) {
        messageLocalDataSource.deleteMessages(conversationId)
        messageRemoteDataSource.deleteMessages(conversationId)
    }

    override suspend fun deleteLocalMessages() {
        messageLocalDataSource.deleteMessages()
    }

    override suspend fun listenRemoteMessages(conversationId: Int) {
        messageRemoteDataSource.listenMessages(conversationId)
            .collect { messages ->
                messages.forEach {
                    messageLocalDataSource.upsertMessage(it)
                }
            }
    }
}