package com.upsaclay.message.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.GetMessagesUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import com.upsaclay.message.domain.usecase.UpdateMessageUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime

private const val MESSAGE_LIMIT = 20

class ChatViewModel(
    conversation: ConversationUI,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val createConversationUseCase: CreateConversationUseCase,
    private val updateMessageUseCase: UpdateMessageUseCase
): ViewModel() {
    private val currentUser: User? = getCurrentUserUseCase().value
    private val _messages = MutableStateFlow<Map<String, Message>>(mapOf())
    val messages: Flow<List<Message>> = _messages.map { messageMap ->
        messageMap.values.toList().sortedByDescending { it.date }
    }
    var conversation = conversation
        private set
    var textToSend: String by mutableStateOf("")
        private set
    private var messagesOffset = MESSAGE_LIMIT
    private var allOldMessageLoaded = false

    init {
        fetchMessages()
        seeMessage()
    }

    fun updateTextToSend(text: String) {
        this.textToSend = text
    }

    fun sendMessage() {
        if (textToSend.isBlank()) return

        if (currentUser == null) throw IllegalArgumentException("User not logged in")

        val message = Message(
            id = GenerateIdUseCase(),
            conversationId = conversation.id,
            senderId = currentUser.id,
            content = textToSend,
            date = LocalDateTime.now(),
            state = MessageState.LOADING
        )

        _messages.value = _messages.value.toMutableMap().apply { put(message.id, message) }

        try {
            if (conversation.state == ConversationState.NOT_CREATED) {
                createConversationUseCase(conversation)
                conversation = conversation.copy(state = ConversationState.CREATED)
            }

            sendMessageUseCase(message)
        } catch (e: Exception) {
            _messages.value = _messages.value.toMutableMap().apply {
                put(message.id, message.copy(state = MessageState.ERROR))
            }
        }

        textToSend = ""
    }

    fun loadOldMessages() {
        if (_messages.value.size < MESSAGE_LIMIT || allOldMessageLoaded) return
        val currentMessages = _messages.value.values.toList()

        viewModelScope.launch {
            getMessagesUseCase(conversation.id, messagesOffset, MESSAGE_LIMIT)
                .apply { messagesOffset += size }
                .forEach {
                    _messages.value = _messages.value.toMutableMap().apply { put(it.id, it) }
                }
            allOldMessageLoaded = currentMessages.size == _messages.value.size
        }
    }

    private fun seeMessage() {
        viewModelScope.launch {
            _messages.collect { messages ->
                messages.values.filter {
                    it.senderId != currentUser?.id && !it.seen
                }.forEach { message ->
                    updateMessageUseCase(message.copy(seen = true))
                }
            }
        }
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            getMessagesUseCase(conversation.id).collect { message ->
                _messages.value = _messages.value.toMutableMap().apply { put(message.id, message) }
            }
        }
    }
}