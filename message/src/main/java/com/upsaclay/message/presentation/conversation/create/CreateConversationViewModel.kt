package com.upsaclay.message.presentation.conversation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.repository.ConversationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

class CreateConversationViewModel(
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateConversationUiState())
    val uiState: StateFlow<CreateConversationUiState> = _uiState

    private var defaultUsers: List<User> = emptyList()
    private val user: Flow<User?> = userRepository.user

    init {
        fetchUsers()
        listenQuery()
    }

    fun getConversation(interlocutor: User): Conversation {
        return runBlocking { conversationRepository.getConversationFromLocal(interlocutor.id) }
            ?: newConversation(interlocutor)
    }

    private fun newConversation(interlocutor: User): Conversation {
        return Conversation(
            id = GenerateIdUseCase.intId,
            interlocutor = interlocutor,
            createdAt = LocalDateTime.now(),
            state = ConversationState.NOT_CREATED
        )
    }

    fun onQueryChange(userName: String) {
        _uiState.update {
            it.copy(
                query = userName
            )
        }
    }

    private fun listenQuery() {
        viewModelScope.launch {
            _uiState
                .distinctUntilChangedBy { it.query }
                .map { it.query }
                .collectLatest { query ->
                    _uiState.update { it.copy(loading = true) }
                    delay(500)
                    getFilteredUsers(query)
                        .also { users ->
                            _uiState.update {
                                it.copy(
                                    users = users,
                                    loading = false
                                )
                            }
                        }
                }
        }
    }

    private fun fetchUsers() {
        _uiState.update {
            it.copy(
                loading = true
            )
        }

        viewModelScope.launch {
            userRepository.getUsers()
                .filter { it.id != user.firstOrNull()?.id }
                .also { users ->
                    _uiState.update {
                        it.copy(
                            users = users,
                            loading = false
                        )
                    }

                    defaultUsers = users
                }
        }
    }

    private fun getFilteredUsers(query: String): List<User> {
       return if (query.isBlank()) {
            defaultUsers
        } else {
           defaultUsers.filter {
               it.firstName.contains(query, ignoreCase = true) ||
                       it.lastName.contains(query, ignoreCase = true)
           }
        }
    }

    data class CreateConversationUiState(
        val users: List<User> = emptyList(),
        val query: String = "",
        val loading: Boolean = true
    )
}