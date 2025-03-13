package com.upsaclay.message.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.ConvertDateUseCase
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.message.domain.entity.ChatEvent
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import com.upsaclay.message.domain.entity.Seen
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.GetLastMessageUseCase
import com.upsaclay.message.domain.usecase.GetMessagesUseCase
import com.upsaclay.message.domain.usecase.GetUnreadMessagesUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import com.upsaclay.message.domain.usecase.UpdateMessageUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

class ChatViewModel(
    private var conversation: ConversationUI,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    getMessagesUseCase: GetMessagesUseCase,
    private val getLastMessageUseCase: GetLastMessageUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val createConversationUseCase: CreateConversationUseCase,
    private val updateMessageUseCase: UpdateMessageUseCase,
    private val getUnreadMessagesUseCase: GetUnreadMessagesUseCase
): ViewModel() {
    private val _event = MutableSharedFlow<ChatEvent>()
    val event: Flow<ChatEvent> = _event
    private val currentUser: User? = getCurrentUserUseCase().value
    val messages: Flow<PagingData<Message>> = getMessagesUseCase(conversation.id).cachedIn(viewModelScope)
    var textToSend: String by mutableStateOf("")
        private set

    init {
        seeMessage()
        newMessageReceived()
        newMessageSent()
    }

    fun updateTextToSend(text: String) {
        this.textToSend = text
    }

    fun sendMessage() {
        if (currentUser == null) throw IllegalArgumentException("User not logged in")
        if (textToSend.isBlank()) return

        val message = Message(
            id = GenerateIdUseCase.asInt(),
            conversationId = conversation.id,
            senderId = currentUser.id,
            content = textToSend,
            date = LocalDateTime.now(),
            state = MessageState.LOADING
        )

        viewModelScope.launch {
            try {
                if (conversation.state == ConversationState.NOT_CREATED) {
                    createConversationUseCase(conversation)
                    conversation = conversation.copy(state = ConversationState.CREATED)
                }

                sendMessageUseCase(message)
            } catch (e: Exception) {
                updateMessageUseCase(message.copy(state = MessageState.ERROR))
            }
        }

        textToSend = ""
    }

    private fun seeMessage() {
        viewModelScope.launch {
            getUnreadMessagesUseCase(conversation.id).collectLatest { messages ->
                messages
                    .filter { it.senderId != currentUser?.id }
                    .map { updateMessageUseCase(it.copy(seen = Seen())) }
            }
        }
    }

    private fun newMessageReceived() {
        getLastMessageUseCase(conversation.id)
            .filter { it.senderId != currentUser?.id }
            .filter { Duration.between(it.date, LocalDateTime.now()).toMinutes() < 1L }
            .map { _event.emit(ChatEvent.NewMessageReceived(ConvertDateUseCase.toTimestamp(it.date))) }
            .launchIn(viewModelScope)
    }

    private fun newMessageSent() {
        getLastMessageUseCase(conversation.id)
            .filter { it.senderId == currentUser?.id }
            .filter { Duration.between(it.date, LocalDateTime.now()).toMinutes() < 1L }
            .map { _event.emit(ChatEvent.NewMessageReceived(ConvertDateUseCase.toTimestamp(it.date))) }
            .launchIn(viewModelScope)
    }
}