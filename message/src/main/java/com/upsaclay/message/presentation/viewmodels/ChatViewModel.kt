package com.upsaclay.message.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.common.domain.userFixture2
import com.upsaclay.message.domain.entity.ChatEvent
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import com.upsaclay.message.domain.entity.Seen
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ChatViewModel(
    private var conversation: Conversation,
    userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val createConversationUseCase: CreateConversationUseCase
): ViewModel() {
    private val _event = MutableSharedFlow<ChatEvent>()
    val event: Flow<ChatEvent> = _event
    private val currentUser: User? = userRepository.currentUser.value
    val messages: Flow<PagingData<Message>> = messageRepository.getMessages(conversation.id).cachedIn(viewModelScope)
    var textToSend: String by mutableStateOf("")
        private set

    init {
        seeMessage()
        listenLastMessage()
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
            recipientId = userFixture2.id,
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

                messageRepository.createMessage(message)
            } catch (e: Exception) {
                messageRepository.updateMessage(message.copy(state = MessageState.ERROR))
            }
        }

        textToSend = ""
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
        messageRepository.getLastMessage(conversation.id)
            .map { _event.emit(ChatEvent.NewMessage(it)) }
            .launchIn(viewModelScope)
    }
}