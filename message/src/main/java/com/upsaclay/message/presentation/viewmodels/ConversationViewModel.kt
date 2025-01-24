package com.upsaclay.message.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.GetUsersUseCase
import com.upsaclay.message.domain.entity.ConversationScreenState
import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.usecase.GetConversationsUseCase
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val getConversationsUseCase: GetConversationsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: Flow<User?> = _currentUser
    private val _screenState = MutableStateFlow(ConversationScreenState.DEFAULT)
    val screenState: Flow<ConversationScreenState> = _screenState
    private val _conversations = MutableStateFlow<List<ConversationUI>>(emptyList())
    val conversations: Flow<List<ConversationUI>> = _conversations

    init {
        initCurrentUser()
        fetchConversations()
    }

    private fun initCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collectLatest { user ->
                _currentUser.value = user
            }
        }
    }

    private fun fetchConversations() {
        viewModelScope.launch {
            _screenState.value = ConversationScreenState.LOADING
            getConversationsUseCase().collect { conversationUI ->
                _conversations.value = _conversations.value.toMutableList().apply { add(conversationUI) }
                _screenState.value = ConversationScreenState.DEFAULT
            }
        }
    }
}