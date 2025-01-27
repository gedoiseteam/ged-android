package com.upsaclay.message

import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetConversationsUseCase
import com.upsaclay.message.domain.usecase.GetConversationUseCase
import com.upsaclay.message.domain.usecase.GetMessagesUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import com.upsaclay.message.presentation.viewmodels.ChatViewModel
import com.upsaclay.message.presentation.viewmodels.ConversationViewModel
import com.upsaclay.message.presentation.viewmodels.CreateConversationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val messageModule = module {
    viewModelOf(::ConversationViewModel)
    viewModelOf(::CreateConversationViewModel)
    viewModel { (conversation: ConversationUI) ->
        ChatViewModel(
            conversation = conversation,
            getCurrentUserUseCase = get(),
            getMessagesUseCase = get(),
            sendMessageUseCase = get(),
            createConversationUseCase = get()
        )
    }

    single<CoroutineScope> { CoroutineScope(Dispatchers.IO) }
    singleOf(::CreateConversationUseCase)
    singleOf(::DeleteConversationUseCase)
    singleOf(::GetConversationsUseCase)
    singleOf(::GetConversationUseCase)
    singleOf(::GetMessagesUseCase)
    singleOf(::SendMessageUseCase)
}