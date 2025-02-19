package com.upsaclay.message

import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.presentation.viewmodels.ChatViewModel
import com.upsaclay.message.presentation.viewmodels.ConversationViewModel
import com.upsaclay.message.presentation.viewmodels.CreateConversationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val messageModule = module {
    viewModelOf(::ConversationViewModel)
    viewModelOf(::CreateConversationViewModel)
    viewModel { (conversationUser: ConversationUser) ->
        ChatViewModel(
            conversationUser = conversationUser,
            getCurrentUserUseCase = get(),
            getMessagesUseCase = get(),
            sendMessageUseCase = get(),
            createConversationUseCase = get()
        )
    }
}