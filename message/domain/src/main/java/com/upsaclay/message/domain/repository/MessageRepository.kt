package com.upsaclay.message.domain.repository

import com.upsaclay.message.domain.entity.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessages(conversationId: Int): Flow<List<Message>>

    fun getUnreadMessages(conversationId: Int): Flow<List<Message>>

    fun getRemoteMessages(conversationId: Int): Flow<List<Message>>

    suspend fun addMessage(message: Message)

    suspend fun updateMessage(message: Message)

    suspend fun upsertMessage(message: Message)

    suspend fun deleteLocalMessages()

    suspend fun deleteMessages(conversationId: Int)

    suspend fun listenRemoteMessages(conversationId: Int)
}