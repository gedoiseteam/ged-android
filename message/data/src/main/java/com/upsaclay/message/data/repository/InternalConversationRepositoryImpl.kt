package com.upsaclay.message.data.repository

import com.upsaclay.message.data.local.ConversationLocalDataSource
import com.upsaclay.message.data.local.model.LocalConversation
import com.upsaclay.message.data.mapper.ConversationMapper
import com.upsaclay.message.data.model.ConversationDTO
import com.upsaclay.message.data.remote.ConversationRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class InternalConversationRepositoryImpl(
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val conversationRemoteDataSource: ConversationRemoteDataSource,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : InternalConversationRepository {
    private val _conversationsDTO = MutableStateFlow<List<ConversationDTO>>(emptyList())
    override val conversationsDTO: Flow<List<ConversationDTO>> = _conversationsDTO

    init {
        scope.launch {
            conversationLocalDataSource.getAllConversations().collect { localConversations ->
                _conversationsDTO.value = localConversations.map(ConversationMapper::toDTO)
            }
        }
    }

    override suspend fun listenRemoteConversations(userId: Int) =
        conversationRemoteDataSource.listenAllConversations(userId)

    override suspend fun insertLocalConversation(localConversation: LocalConversation) {
        conversationLocalDataSource.insertConversation(localConversation)
    }

    override suspend fun createConversation(conversationDTO: ConversationDTO) {
        val remoteConversation = ConversationMapper.toRemote(conversationDTO)
        val localConversation = ConversationMapper.toLocal(conversationDTO)

        conversationLocalDataSource.insertConversation(localConversation)
        conversationRemoteDataSource.createConversation(remoteConversation)
            .onSuccess { conversationLocalDataSource.updateConversation(localConversation.copy(isSynchronized = true)) }
    }

    override suspend fun setConversationActive(conversationDTO: ConversationDTO) {
        val localConversation = ConversationMapper.toLocal(conversationDTO)
        conversationLocalDataSource.setConversationActive(localConversation)
        conversationRemoteDataSource.setConversationActive(conversationDTO.conversationId)
    }

    override suspend fun deleteConversation(conversationDTO: ConversationDTO) {
        val localConversation = ConversationMapper.toLocal(conversationDTO)
        conversationLocalDataSource.deleteConversation(localConversation)
        conversationRemoteDataSource.deleteConversation(conversationDTO.conversationId)
    }
}