package com.upsaclay.message.data.repository

import com.upsaclay.message.data.local.MessageLocalDataSource
import com.upsaclay.message.data.remote.MessageRemoteDataSource
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import com.upsaclay.message.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

internal class MessageRepositoryImpl(
    private val messageLocalDataSource: MessageLocalDataSource,
    private val messageRemoteDataSource: MessageRemoteDataSource,
    private val scope: CoroutineScope = (GlobalScope + Dispatchers.IO)
): MessageRepository {
    override fun getMessages(conversationId: String): Flow<Message> {
        listenRemoteMessage(conversationId)
        return messageLocalDataSource.getMessages(conversationId)
    }

    override fun getLastMessage(conversationId: String): Flow<Message?> =
        messageRemoteDataSource.listenLastMessage(conversationId)

    override suspend fun createMessage(message: Message) {
        messageLocalDataSource.insertMessage(message)
        messageRemoteDataSource.createMessage(message)
    }

    override suspend fun updateMessage(message: Message) {
        messageLocalDataSource.updateMessage(message)
    }

    override suspend fun upsertMessage(message: Message) {
        messageLocalDataSource.upsertMessage(message)
    }

    private fun listenRemoteMessage(conversationId: String) {
        scope.launch {
            messageRemoteDataSource.listenMessages(conversationId).collect {
                messageLocalDataSource.upsertMessage(it)
            }
        }
    }
}