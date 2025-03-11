package com.upsaclay.message.data.remote

import com.upsaclay.message.data.mapper.MessageMapper
import com.upsaclay.message.data.remote.api.MessageApi
import com.upsaclay.message.domain.entity.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class MessageRemoteDataSource(private val messageApi: MessageApi) {
    fun listenMessages(conversationId: Int): Flow<Message> =
        messageApi.listenMessages(conversationId)
            .map(MessageMapper::toDomain)

    suspend fun createMessage(message: Message) {
        withContext(Dispatchers.IO) {
            messageApi.createMessage(MessageMapper.toRemote(message))
        }
    }

    suspend fun updateMessage(message: Message) {
        withContext(Dispatchers.IO) {
            messageApi.updateSeenMessage(MessageMapper.toRemote(message))
        }
    }

    suspend fun deleteMessages(conversationId: Int) {
        withContext(Dispatchers.IO) {
            messageApi.deleteMessages(conversationId)
        }
    }
}