package com.upsaclay.message

import com.upsaclay.common.domain.e
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetConversationUserUseCase
import com.upsaclay.message.domain.usecase.GetConversationsUIUseCase
import com.upsaclay.message.domain.usecase.GetMessagesUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import com.upsaclay.message.presentation.viewmodels.ChatViewModel
import com.upsaclay.message.presentation.viewmodels.ConversationViewModel
import com.upsaclay.message.presentation.viewmodels.CreateConversationViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val BACKGROUND_SCOPE = named("BackgroundScope")

val messageModule = module {
    single<CoroutineScope>(BACKGROUND_SCOPE) {
        CoroutineScope(
            SupervisorJob() +
                    Dispatchers.IO +
                    CoroutineExceptionHandler { coroutineContext, throwable ->
                        e("Uncaught error in backgroundScope", throwable)
                    }
        )
    }

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

    single {
        GetConversationsUIUseCase(
            userConversationRepository = get(),
            messageRepository = get(),
            scope = get(BACKGROUND_SCOPE)
        )
    }
    singleOf(::CreateConversationUseCase)
    singleOf(::DeleteConversationUseCase)
    singleOf(::GetConversationUserUseCase)
    singleOf(::GetMessagesUseCase)
    singleOf(::SendMessageUseCase)
}