package com.upsaclay.message.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.message.domain.entity.ConversationScreenState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.ListenConversationsUiUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException

class ConversationViewModel(
    listenConversationsUiUseCase: ListenConversationsUiUseCase,
    private val deleteConversationUseCase: DeleteConversationUseCase
) : ViewModel() {
    private val _screenState = MutableStateFlow(ConversationScreenState.DEFAULT)
    val screenState: StateFlow<ConversationScreenState> = _screenState
    val conversations: Flow<List<ConversationUI>> = listenConversationsUiUseCase.conversationsUI

    fun deleteConversation(conversation: ConversationUI) {
        viewModelScope.launch {
            try {
                deleteConversationUseCase(conversation)
                _screenState.value = ConversationScreenState.SUCCESS
            } catch (e: Exception) {
                _screenState.value = ConversationScreenState.ERROR
            }
        }
    }
}