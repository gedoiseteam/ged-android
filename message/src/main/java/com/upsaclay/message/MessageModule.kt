package com.upsaclay.message

import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.GetAllConversationsUseCase
import com.upsaclay.message.domain.usecase.GetConversationUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import com.upsaclay.message.domain.usecase.StartConversationUseCase
import com.upsaclay.message.presentation.viewmodel.ChatViewModel
import com.upsaclay.message.presentation.viewmodel.ConversationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val messageModule = module {
    viewModelOf(::ConversationViewModel)
    viewModelOf(::ChatViewModel)

    single<CoroutineScope> { CoroutineScope(Dispatchers.IO) }
    singleOf(::CreateConversationUseCase)
    singleOf(::GetAllConversationsUseCase)
    singleOf(::GetConversationUseCase)
    singleOf(::SendMessageUseCase)
    singleOf(::StartConversationUseCase)
}