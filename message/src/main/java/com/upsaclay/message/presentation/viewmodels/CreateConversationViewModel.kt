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
import com.upsaclay.message.domain.usecase.GetFilteredUserUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CreateConversationViewModel(
    private val userConversationRepository: UserConversationRepository,
    private val userRepository: UserRepository,
    private val getFilteredUserUseCase: GetFilteredUserUseCase
) : ViewModel() {
    private var defaultUsers: List<User> = emptyList()
    private val _event = MutableSharedFlow<ConversationEvent>()
    val event: SharedFlow<ConversationEvent> = _event
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: Flow<List<User>> = _users
    private val _query = MutableStateFlow("")
    val query: Flow<String> = _query
    private val currentUser: User? = userRepository.currentUser.value

    init {
        fetchUsers()
        getFilteredUsers()
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

    fun updateSearchedUser(userName: String) {
        _query.value = userName
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            _event.emit(ConversationEvent.Loading)
            _users.value = userRepository.getUsers().filterNot { it.id == currentUser?.id }
            defaultUsers = _users.value
            _event.emit(ConversationEvent.Success(SuccessType.LOADED))
        }
    }

    private fun getFilteredUsers() {
        viewModelScope.launch {
            _event.emit(ConversationEvent.Loading)
            _query
                .filter {
                   if (it.isBlank()) {
                        _users.value = defaultUsers
                        _event.emit(ConversationEvent.Success(SuccessType.LOADED))
                        false
                    } else {
                        true
                    }
                }
                .collectLatest { query ->
                    delay(500)
                    getFilteredUserUseCase(query)
                        .filterNot { it.id == currentUser?.id }
                        .let { filteredUsers ->
                            _event.emit(ConversationEvent.Success(SuccessType.LOADED))
                            _users.value = filteredUsers
                        }
                }
        }
    }
}