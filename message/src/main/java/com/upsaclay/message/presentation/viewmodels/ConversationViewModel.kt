package com.upsaclay.message.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.usecase.GetConversationsUIUseCase
import kotlinx.coroutines.flow.Flow

class ConversationViewModel(
    getConversationsUIUseCase: GetConversationsUIUseCase
) : ViewModel() {
    val conversations: Flow<List<ConversationUI>> = getConversationsUIUseCase()
}