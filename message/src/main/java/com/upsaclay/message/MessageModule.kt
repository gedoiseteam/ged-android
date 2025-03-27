package com.upsaclay.message

import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.usecase.GetNewConversationMessageUseCase
import com.upsaclay.message.presentation.viewmodels.ChatViewModel
import com.upsaclay.message.presentation.viewmodels.ConversationViewModel
import com.upsaclay.message.presentation.viewmodels.CreateConversationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val messageModule = module {
    viewModelOf(::ConversationViewModel)
    viewModelOf(::CreateConversationViewModel)
    viewModel { (conversation: Conversation) ->
        ChatViewModel(
            conversation = conversation,
            getCurrentUserUseCase = get(),
            messageRepository = get(),
            createConversationUseCase = get()
        )
    }

    singleOf(::GetNewConversationMessageUseCase)
}