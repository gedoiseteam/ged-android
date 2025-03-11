package com.upsaclay.message.domain.repository

import androidx.paging.PagingData
import com.upsaclay.message.domain.entity.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessages(conversationId: Int): Flow<PagingData<Message>>

    fun getLastMessage(conversationId: Int): Flow<Message>

    suspend fun createMessage(message: Message)

    suspend fun updateMessage(message: Message)

    suspend fun upsertMessage(message: Message)

    suspend fun deleteLocalMessages()

    suspend fun deleteMessages(conversationId: Int)

    suspend fun listenRemoteMessages(conversationId: Int)
}