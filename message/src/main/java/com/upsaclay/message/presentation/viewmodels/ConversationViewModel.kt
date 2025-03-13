package com.upsaclay.message.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.upsaclay.message.domain.entity.ConversationScreenState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetConversationsUIUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConversationViewModel(
    getConversationsUIUseCase: GetConversationsUIUseCase,
    private val deleteConversationUseCase: DeleteConversationUseCase
) : ViewModel() {
    private val _screenState = MutableStateFlow(ConversationScreenState.DEFAULT)
    val screenState: StateFlow<ConversationScreenState> = _screenState
    val conversations: Flow<PagingData<ConversationUI>> = getConversationsUIUseCase().cachedIn(viewModelScope)

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