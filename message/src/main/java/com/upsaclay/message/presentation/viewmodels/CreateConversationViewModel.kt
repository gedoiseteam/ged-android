package com.upsaclay.message.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.GetUsersUseCase
import com.upsaclay.message.domain.entity.ConversationScreenState
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.usecase.ConvertConversationJsonUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CreateConversationViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _screenState = MutableStateFlow(ConversationScreenState.DEFAULT)
    val screenState: Flow<ConversationScreenState> = _screenState
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: Flow<List<User>> = _users
    private val currentUser: User? = getCurrentUserUseCase().value

    init {
        fetchUsers()
    }

    fun generateConversationJson(interlocutor: User): String {
        val conversation = ConversationUI(
            id = GenerateIdUseCase.invoke(),
            interlocutor = interlocutor,
            lastMessage = null,
            createdAt = LocalDateTime.now(),
            state = ConversationState.NOT_CREATED
        )
        return ConvertConversationJsonUseCase.to(conversation)
    }

    private fun fetchUsers() {
        _screenState.value = ConversationScreenState.LOADING
        viewModelScope.launch {
            _users.value = getUsersUseCase().filterNot { it.id == currentUser?.id }
            _screenState.value = ConversationScreenState.DEFAULT
        }
    }
}