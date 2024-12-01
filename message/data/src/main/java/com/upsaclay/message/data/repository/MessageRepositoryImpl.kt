package com.upsaclay.message.data.repository

import com.upsaclay.message.data.local.MessageLocalDataSource
import com.upsaclay.message.data.mapper.MessageMapper
import com.upsaclay.message.data.remote.MessageRemoteDataSource
import com.upsaclay.message.domain.model.Message
import com.upsaclay.message.domain.repository.MessageRepository

internal class MessageRepositoryImpl(
    private val messageLocalDataSource: MessageLocalDataSource,
    private val messageRemoteDataSource: MessageRemoteDataSource
): MessageRepository {
    override suspend fun sendMessage(conversationId: String, message: Message, currentUserId: String): Result<Unit> {
        val messageDTO = MessageMapper.toDTO(conversationId, message, currentUserId)
        val localMessage = MessageMapper.toLocal(messageDTO)
        messageLocalDataSource.insertMessage(localMessage)

        val remoteMessage = MessageMapper.toRemote(messageDTO)
        return messageRemoteDataSource.addMessage(conversationId, remoteMessage)
            .onSuccess { messageLocalDataSource.updateMessage(localMessage.copy(isSent = true)) }
    }
}