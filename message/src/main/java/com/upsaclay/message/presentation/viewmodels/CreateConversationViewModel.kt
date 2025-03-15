package com.upsaclay.message.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.GetUsersUseCase
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationEvent
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.SuccessType
import com.upsaclay.message.domain.usecase.ConvertConversationJsonUseCase
import com.upsaclay.message.domain.usecase.GetConversationUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CreateConversationViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val getConversationUseCase: GetConversationUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _event = MutableSharedFlow<ConversationEvent>()
    val event: Flow<ConversationEvent> = _event
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: Flow<List<User>> = _users
    private val currentUser: User? = getCurrentUserUseCase().value

    init {
        fetchUsers()
    }

    fun generateConversationJson(interlocutor: User): String {
        val conversation = ConversationUI(
            id = GenerateIdUseCase.asInt(),
            interlocutor = interlocutor,
            lastMessage = null,
            createdAt = LocalDateTime.now(),
            state = ConversationState.NOT_CREATED
        )
        return ConvertConversationJsonUseCase(conversation)
    }

    suspend fun getConversation(interlocutorId: String): Conversation? = getConversationUseCase(interlocutorId)

    private fun fetchUsers() {
        viewModelScope.launch {
            _event.emit(ConversationEvent.Loading)
            _users.value = getUsersUseCase().filterNot { it.id == currentUser?.id }
            _event.emit(ConversationEvent.Success(SuccessType.LOADED))
        }
    }
}