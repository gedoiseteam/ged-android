package com.upsaclay.message.data.repository

import com.upsaclay.message.data.local.MessageLocalDataSource
import com.upsaclay.message.data.remote.MessageRemoteDataSource
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

internal class MessageRepositoryImpl(
    private val messageLocalDataSource: MessageLocalDataSource,
    private val messageRemoteDataSource: MessageRemoteDataSource
): MessageRepository {
    override fun getMessages(conversationId: String): Flow<Message> = messageLocalDataSource.getMessages(conversationId)

    override fun getLastMessage(conversationId: String): Flow<Message> =
        messageLocalDataSource.getLastMessage(conversationId)

    override suspend fun getMessages(conversationId: String, limit: Int, offset: Int): List<Message> =
        messageLocalDataSource.getMessages(conversationId, limit, offset)

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

    override suspend fun deleteMessages(conversationId: String) {
        messageLocalDataSource.deleteMessages(conversationId)
        messageRemoteDataSource.deleteMessages(conversationId)
    }

    override suspend fun deleteLocalMessages() {
        messageLocalDataSource.deleteMessages()
    }

    override suspend fun listenRemoteMessages(conversationId: String) {
        messageRemoteDataSource.listenMessages(conversationId)
            .collect { messageLocalDataSource.upsertMessage(it) }
    }
}