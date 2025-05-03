package com.upsaclay.message.presentation.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.SingleUiEvent
import com.upsaclay.common.domain.entity.TooManyRequestException
import com.upsaclay.message.R
import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.repository.ConversationMessageRepository
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException

class ConversationViewModel(
    private val conversationMessageRepository: ConversationMessageRepository,
    private val deleteConversationUseCase: DeleteConversationUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState: StateFlow<ConversationUiState> = _uiState

    private val _event = MutableSharedFlow<SingleUiEvent>()
    val event: SharedFlow<SingleUiEvent> = _event

    init {
        listenConversations()
    }

    fun deleteConversation(conversation: Conversation) {
        viewModelScope.launch {
            runCatching {
                deleteConversationUseCase(conversation)
            }.onSuccess {
                _event.emit(SingleUiEvent.Success(R.string.conversation_deleted))
            }.onFailure {
                _event.emit(SingleUiEvent.Error(mapToErrorMessage(it)))
            }
        }
    }

    private fun listenConversations() {
        viewModelScope.launch {
            conversationMessageRepository.conversationsMessage
                .map { conversationMessages ->
                    conversationMessages.map { conversationMessage ->
                        ConversationMapper.toConversationUI(conversationMessage)
                    }
                }.collectLatest { conversations ->
                    _uiState.update {
                        it.copy(conversations = conversations)
                    }
                }
        }
    }

    private fun mapToErrorMessage(exception: Throwable): Int {
        return when (exception) {
            is ConnectException -> com.upsaclay.common.R.string.unknown_network_error
            is TooManyRequestException -> com.upsaclay.common.R.string.too_many_request_error
            else -> com.upsaclay.common.R.string.unknown_error
        }
    }

    data class ConversationUiState(
        val conversations: List<ConversationUI> = emptyList(),
        val loading: Boolean = false
    )
}