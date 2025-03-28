package com.upsaclay.message.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationEvent
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.SuccessType
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CreateConversationViewModel(
    private val userConversationRepository: UserConversationRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _event = MutableSharedFlow<ConversationEvent>()
    val event: Flow<ConversationEvent> = _event
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: Flow<List<User>> = _users
    private val currentUser: User? = userRepository.currentUser.value

    init {
        fetchUsers()
    }

    fun generateConversation(interlocutor: User): Conversation {
        return Conversation(
            id = GenerateIdUseCase.asInt(),
            interlocutor = interlocutor,
            createdAt = LocalDateTime.now(),
            state = ConversationState.NOT_CREATED
        )
    }

    suspend fun getConversation(interlocutorId: String): Conversation? = userConversationRepository.getConversation(interlocutorId)

    private fun fetchUsers() {
        viewModelScope.launch {
            _event.emit(ConversationEvent.Loading)
            _users.value = userRepository.getUsers().filterNot { it.id == currentUser?.id }
            _event.emit(ConversationEvent.Success(SuccessType.LOADED))
        }
    }
}