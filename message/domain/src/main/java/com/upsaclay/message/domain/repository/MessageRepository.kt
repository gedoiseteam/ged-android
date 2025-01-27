package com.upsaclay.message.domain.repository

import com.upsaclay.message.domain.entity.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessages(conversationId: String): Flow<Message>

    fun getLastMessage(conversationId: String): Flow<Message?>

    suspend fun createMessage(message: Message)

    suspend fun updateMessage(message: Message)

    suspend fun upsertMessage(message: Message)

    suspend fun deleteLocalMessages()
}