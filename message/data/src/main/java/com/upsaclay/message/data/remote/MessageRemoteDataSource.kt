package com.upsaclay.message.data.remote

import com.upsaclay.message.data.mapper.MessageMapper
import com.upsaclay.message.data.remote.api.MessageApi
import com.upsaclay.message.domain.entity.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class MessageRemoteDataSource(private val messageApi: MessageApi) {
    fun listenMessages(conversationId: String): Flow<Message> =
        messageApi.listenMessages(conversationId)
            .map(MessageMapper::toDomain)

    fun listenLastMessage(conversationId: String): Flow<Message?> =
        messageApi.listenLastMessage(conversationId)
            .map { it?.let(MessageMapper::toDomain) }

    suspend fun getMessages(conversationId: String, limit: Long): List<Message> =
        messageApi.getMessages(conversationId, limit).map(MessageMapper::toDomain)

    suspend fun createMessage(message: Message) {
        withContext(Dispatchers.IO) {
            messageApi.createMessage(MessageMapper.toRemote(message))
        }
    }
}