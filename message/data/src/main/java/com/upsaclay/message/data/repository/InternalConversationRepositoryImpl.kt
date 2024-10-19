package com.upsaclay.message.data.repository

import com.upsaclay.message.data.local.ConversationLocalDataSource
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.data.model.ConversationDTO
import com.upsaclay.message.data.remote.ConversationRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class InternalConversationRepositoryImpl(
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val conversationRemoteDataSource: ConversationRemoteDataSource
) : InternalConversationRepository {
    private val _conversationsDTO = MutableStateFlow<List<ConversationDTO>>(emptyList())
    override val conversationsDTO: StateFlow<List<ConversationDTO>> = _conversationsDTO

    init {
        CoroutineScope(Dispatchers.IO).launch {
            conversationLocalDataSource.getAllConversations().collect { localConversations ->
                _conversationsDTO.value = localConversations.map { ConversationMapper.toDTO(it) }
            }
        }
    }

    override suspend fun listenRemoteConversations(userId: Int) {
        conversationRemoteDataSource.listenAllConversations(userId).collect { remoteConversations ->
            remoteConversations.forEach {
                conversationLocalDataSource.insertConversation(ConversationMapper.toLocal(it))
            }
        }
    }

    override suspend fun createConversation(conversationDTO: ConversationDTO) {
        conversationLocalDataSource.insertConversation(ConversationMapper.toLocal(conversationDTO))
    }
}