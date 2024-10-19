package com.upsaclay.message.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.GetAllUserUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.message.domain.model.Conversation
import com.upsaclay.message.domain.model.ConversationState
import com.upsaclay.message.domain.usecase.GetAllConversationsUseCase
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConversationViewModel(
    getAllConversationsUseCase: GetAllConversationsUseCase,
    private val createConversationUseCase: CreateConversationUseCase,
    private val getAllUserUseCase: GetAllUserUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: Flow<List<User>> = _users
    private val currentUser = getCurrentUserUseCase()
    private val _conversationState = MutableStateFlow(ConversationState.DEFAULT)
    val conversationState: Flow<ConversationState> = _conversationState
    val conversations: Flow<List<Conversation>> = getAllConversationsUseCase()

    init {
        viewModelScope.launch {
            _conversationState.value = ConversationState.LOADING
            getAllUserUseCase().collectLatest { usersList ->
                currentUser?.let {
                    _users.value = usersList.filterNot { it.id == currentUser.id }
                    _conversationState.value = ConversationState.DEFAULT
                }
            }
        }
    }

    fun createNewConversation(interlocutor: User) {
        _conversationState.value = ConversationState.LOADING
        viewModelScope.launch {
            createConversationUseCase(interlocutor)
            _conversationState.value = ConversationState.CREATED
        }
    }
}