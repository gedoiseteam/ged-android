package com.upsaclay.message.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.d
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.message.domain.entity.ConversationScreenState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.usecase.GetConversationsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val getConversationsUseCase: GetConversationsUseCase
) : ViewModel() {
    private val _screenState = MutableStateFlow(ConversationScreenState.DEFAULT)
    val screenState: Flow<ConversationScreenState> = _screenState
    private val _conversations = MutableStateFlow<Map<String, ConversationUI>>(mapOf())
    val conversations: Flow<List<ConversationUI>> = _conversations.map { conversationMap ->
        conversationMap.values.toList().sortedByDescending {
            it.lastMessage?.date ?: it.createdAt
        }
    }

    init {
        fetchConversations()
    }

    private fun fetchConversations() {
        viewModelScope.launch {
            _screenState.value = ConversationScreenState.LOADING
            getConversationsUseCase().collect { conversationUI ->
                _conversations.value += (conversationUI.id to conversationUI)
                _screenState.value = ConversationScreenState.DEFAULT
            }
        }
    }
}