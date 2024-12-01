package com.upsaclay.message.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.usecase.GetCurrentUserFlowUseCase
import com.upsaclay.message.domain.model.Conversation
import com.upsaclay.message.domain.model.Message
import com.upsaclay.message.domain.model.MessageType
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetConversationUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class ChatViewModel(
    getCurrentUserFlowUseCase: GetCurrentUserFlowUseCase,
    private val getConversationUseCase: GetConversationUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val deleteConversationUseCase: DeleteConversationUseCase
): ViewModel() {
    private val _conversation = MutableStateFlow<Conversation?>(null)
    val conversation: Flow<Conversation> = _conversation.filterNotNull()
    var messageToSend: String by mutableStateOf("")
        private set

    fun updateMessageToSend(text: String) {
        this.messageToSend = text
    }

    fun resetMessageToSend() {
        this.messageToSend = ""
    }

    fun sendMessage() {
        viewModelScope.launch {
            val message = Message(
                sentByUser = true,
                content = messageToSend,
                type = MessageType.TEXT
            )

            sendMessageUseCase(_conversation.value!!, message)
        }
    }

    fun getConversation(interlocutorId: String) {
        viewModelScope.launch {
            getConversationUseCase(interlocutorId).collect {
                _conversation.value = it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _conversation.value?.let { conversation ->
            if(conversation.messages.isEmpty() && !conversation.isActive) {
                GlobalScope.launch(Dispatchers.IO) {
                    deleteConversationUseCase(conversation)
                }
            }
        }
    }
}