package com.upsaclay.message.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.GenerateIDUseCase
import com.upsaclay.common.domain.usecase.GetUsersUseCase
import com.upsaclay.message.domain.entity.ConversationScreenState
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CreateConversationViewModel(private val getUsersUseCase: GetUsersUseCase): ViewModel() {
    private val _screenState = MutableStateFlow(ConversationScreenState.DEFAULT)
    val screenState: Flow<ConversationScreenState> = _screenState
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: Flow<List<User>> = _users

    init {
        fetchUsers()
    }

    fun generateConversation(interlocutor: User) = ConversationUI(
        id = GenerateIDUseCase.invoke(),
        interlocutor = interlocutor,
        lastMessage = null,
        createdAt = LocalDateTime.now(),
        state = ConversationState.NOT_CREATED
    )

    private fun fetchUsers() {
        _screenState.value = ConversationScreenState.LOADING
        viewModelScope.launch {
            _users.value = getUsersUseCase()
            _screenState.value = ConversationScreenState.DEFAULT
        }
    }
}