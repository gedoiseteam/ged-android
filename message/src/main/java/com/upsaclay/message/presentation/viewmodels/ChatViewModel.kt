package com.upsaclay.message.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.entity.UserNotFoundException
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.common.domain.usecase.NotificationUseCase
import com.upsaclay.common.domain.userFixture2
import com.upsaclay.message.domain.entity.ChatEvent
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
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
    private val _event = MutableSharedFlow<ChatEvent>()
    val event: Flow<ChatEvent> = _event
    private val currentUser: User? = userRepository.currentUser.value
    val messages: Flow<List<Message>> = messageRepository.getMessages(conversation.id)
    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    init {
        clearChatNotifications()
        seeMessage()
        listenLastMessage()
    }

    fun updateTextToSend(text: String) {
        this._text.value = text
    }

    fun sendMessage() {
        if (currentUser == null) throw UserNotFoundException("User not logged in")

        val message = Message(
            id = GenerateIdUseCase.asInt(),
            conversationId = conversation.id,
            senderId = currentUser.id,
            recipientId = conversation.interlocutor.id,
            content = _text.value,
            date = LocalDateTime.now(),
            state = MessageState.LOADING
        )

        viewModelScope.launch {
            try {
                if (conversation.state == ConversationState.NOT_CREATED) {
                    createConversationUseCase(conversation)
                    conversation = conversation.copy(state = ConversationState.CREATED)
                }
                sendMessageUseCase(currentUser, conversation, message)
            } catch (e: Exception) {
                messageRepository.updateMessage(message.copy(state = MessageState.ERROR))
            }
        }

        _text.value = ""
    }

    private fun seeMessage() {
        viewModelScope.launch {
            messageRepository.getUnreadMessages(conversation.id)
                .onEach { delay(50) }
                .collectLatest { messages ->
                    messages
                        .filter { it.senderId != currentUser?.id }
                        .map { messageRepository.updateMessage(it.copy(seen = Seen())) }
                }
        }
    }

    private fun listenLastMessage() {
        viewModelScope.launch {
            messages
                .distinctUntilChanged()
                .collectLatest {
                    val lastMessage = it.lastOrNull()
                    if (lastMessage != null && lastMessage.senderId != currentUser?.id) {
                        _event.emit(ChatEvent.NewMessage(lastMessage))
                    }
                }
        }
    }

    private fun clearChatNotifications() {
        viewModelScope.launch {
            notificationUseCase.clearNotifications(conversation.id.toString())
        }
    }
}