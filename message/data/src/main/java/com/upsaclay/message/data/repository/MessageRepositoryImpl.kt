package com.upsaclay.message.data.repository

import com.upsaclay.message.data.local.MessageLocalDataSource
import com.upsaclay.message.data.remote.MessageRemoteDataSource
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal class MessageRepositoryImpl(
    private val messageLocalDataSource: MessageLocalDataSource,
    private val messageRemoteDataSource: MessageRemoteDataSource,
    private val scope: CoroutineScope
) : MessageRepository {
    private val jobs = mutableListOf<Job>()

    override fun getMessages(conversationId: String): Flow<Message> {
        listenRemoteMessage(conversationId)
        return messageLocalDataSource.getMessages(conversationId)
    }

    private fun listenRemoteMessage(conversationId: String) {
        val job = scope.launch {
            messageRemoteDataSource.listenMessages(conversationId).collect {
                messageLocalDataSource.upsertMessage(it)
            }
        }
        jobs.add(job)
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

    override suspend fun deleteLocalMessages() {
        messageLocalDataSource.deleteMessages()
    }

    override fun stopListenMessages() {
        jobs.forEach { it.cancel() }
    }
}