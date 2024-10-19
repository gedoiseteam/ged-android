package com.upsaclay.message.data.repository

import com.upsaclay.message.data.local.MessageLocalDataSource
import com.upsaclay.message.data.mapper.MessageMapper
import com.upsaclay.message.data.model.MessageDTO
import com.upsaclay.message.data.remote.MessageRemoteDataSource
import com.upsaclay.message.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class InternalMessageRepositoryImpl(
    private val messageRemoteDataSource: MessageRemoteDataSource,
    private val messageLocalDataSource: MessageLocalDataSource
) : InternalMessageRepository {
    override fun listenLastMessages(conversationId: String): Flow<List<MessageDTO>> =
        messageRemoteDataSource.listenLastMessages(conversationId).map { remoteMessages ->
            remoteMessages.map(MessageMapper::toDTO)
        }

    override suspend fun getMessages(conversationId: String): List<MessageDTO> =
        messageLocalDataSource.getMessages(conversationId).map(MessageMapper::toDTO)
}