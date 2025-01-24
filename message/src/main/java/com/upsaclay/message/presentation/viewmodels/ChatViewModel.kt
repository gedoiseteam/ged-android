package com.upsaclay.message.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.GenerateIDUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.GetMessagesUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    conversation: ConversationUI,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val createConversationUseCase: CreateConversationUseCase
): ViewModel() {
    private val _messages = MutableStateFlow<Map<String, Message>>(mapOf())
    val messages: Flow<Map<String, Message>> = _messages
    private var currentUser: User? = null
    var conversation = conversation
        private set
    var textToSend: String by mutableStateOf("")
        private set

    init {
        fetchMessages()
        viewModelScope.launch {
            getCurrentUserUseCase().collect {
                currentUser = it
            }
        }
    }

    fun updateTextToSend(text: String) {
        this.textToSend = text
    }

    fun sendMessage() {
        if (textToSend.isBlank()) return

        if(currentUser == null) throw IllegalArgumentException("User not logged in")

        viewModelScope.launch {
            if(conversation.state == ConversationState.NOT_CREATED) {
                createConversationUseCase(conversation)
                conversation = conversation.copy(state = ConversationState.CREATED)
            }

            val message = Message(
                id = GenerateIDUseCase(),
                conversationId = conversation.id,
                senderId = currentUser!!.id,
                content = textToSend,
                state = MessageState.LOADING
            )

            sendMessageUseCase(message)
        }

        textToSend = ""
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            getMessagesUseCase(conversation.id).collect {
                _messages.value = _messages.value.toMutableMap().apply { put(it.id, it) }
            }
        }
    }
}