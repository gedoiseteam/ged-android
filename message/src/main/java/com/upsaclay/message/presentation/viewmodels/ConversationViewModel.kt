package com.upsaclay.message.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.message.domain.entity.ConversationEvent
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.SuccessType
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetPagedConversationsUIUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ConversationViewModel(
    getPagedConversationsUIUseCase: GetPagedConversationsUIUseCase,
    private val deleteConversationUseCase: DeleteConversationUseCase
) : ViewModel() {
    private val _event = MutableSharedFlow<ConversationEvent>()
    val event: SharedFlow<ConversationEvent> = _event
    val conversations: Flow<PagingData<ConversationUI>> = getPagedConversationsUIUseCase().cachedIn(viewModelScope)

    fun deleteConversation(conversation: ConversationUI) {
        viewModelScope.launch {
            try {
                deleteConversationUseCase(conversation)
                _event.emit(ConversationEvent.Success(SuccessType.DELETED))
            } catch (e: Exception) {
                _event.emit(ConversationEvent.Error(ErrorType.UnknownError))
            }
        }
    }
}