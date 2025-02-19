package com.upsaclay.message.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.usecase.ListenConversationsUiUseCase
import kotlinx.coroutines.flow.Flow

class ConversationViewModel(
    listenConversationsUiUseCase: ListenConversationsUiUseCase
) : ViewModel() {
    val conversations: Flow<List<ConversationUI>> = listenConversationsUiUseCase.conversationsUI
}