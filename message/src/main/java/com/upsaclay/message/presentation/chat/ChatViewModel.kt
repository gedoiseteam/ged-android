package com.upsaclay.message.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.SingleUiEvent
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.entity.UserNotFoundException
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.common.domain.usecase.NotificationUseCase
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import com.upsaclay.message.domain.entity.Seen
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ChatViewModel(
    private var conversation: Conversation,
    userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val sendMessageUseCase: SendMessageUseCase,
    private val createConversationUseCase: CreateConversationUseCase,
    private val notificationUseCase: NotificationUseCase
): ViewModel() {
    private val user: Flow<User?> = userRepository.user
    private val _uiState = MutableStateFlow(
        ChatUiState(
            messages = emptyList(),
            conversation = conversation,
            text = ""
        )
    )
    internal val uiState: StateFlow<ChatUiState> = _uiState
    internal val event: Flow<MessageEvent> = messageEvents()

    init {
        clearChatNotifications()
        setMessagesToSeen()
        listenMessages()
    }

    fun onTextChanged(text: String) {
        _uiState.update {
            it.copy(text = text)
        }
    }

    fun sendMessage() {
        val text = _uiState.value.text.takeUnless { it.isEmpty() } ?: return

        viewModelScope.launch {
            val user = user.firstOrNull() ?: throw UserNotFoundException("User not logged in")

            val message = Message(
                id = GenerateIdUseCase.intId,
                conversationId = conversation.id,
                senderId = user.id,
                recipientId = conversation.interlocutor.id,
                content = text,
                date = LocalDateTime.now(),
                state = MessageState.LOADING
            )

            try {
                if (conversation.state == ConversationState.NOT_CREATED) {
                    createConversationUseCase(conversation)
                    conversation = conversation.copy(state = ConversationState.CREATED)
                }
                sendMessageUseCase(user, conversation, message)
            } catch (e: Exception) {
                messageRepository.updateMessage(message.copy(state = MessageState.ERROR))
            }
        }

        _uiState.update {
            it.copy(text = "")
        }
    }

    private fun setMessagesToSeen() {
        viewModelScope.launch {
            messageRepository.getUnreadMessages(conversation.id)
                .onEach { delay(50) }
                .collectLatest { messages ->
                    messages
                        .filter { it.senderId != user.firstOrNull()?.id }
                        .map { messageRepository.updateMessage(it.copy(seen = Seen())) }
                }
        }
    }

    private fun clearChatNotifications() {
        viewModelScope.launch {
            notificationUseCase.clearNotifications(conversation.id.toString())
        }
    }

    private fun listenMessages() {
        viewModelScope.launch {
            messageRepository.getMessages(conversation.id).collectLatest { messages ->
                _uiState.update {
                    it.copy(messages = messages)
                }
            }
        }
    }

    private fun messageEvents(): Flow<MessageEvent> = uiState
        .map { it.messages }
        .distinctUntilChanged()
        .mapNotNull { it.firstOrNull() }
        .filter { it.senderId != user.firstOrNull()?.id }
        .map { MessageEvent.NewMessage(it) }

    internal data class ChatUiState(
        val messages: List<Message>,
        val conversation: Conversation,
        val text: String
    )

    internal sealed class MessageEvent: SingleUiEvent {
        data class NewMessage(val message: Message): MessageEvent()
    }
}