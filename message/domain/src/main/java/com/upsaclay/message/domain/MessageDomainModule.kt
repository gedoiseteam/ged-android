package com.upsaclay.message.domain

import com.upsaclay.common.domain.e
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetConversationUIUseCase
import com.upsaclay.message.domain.usecase.GetConversationUseCase
import com.upsaclay.message.domain.usecase.GetLastMessageUseCase
import com.upsaclay.message.domain.usecase.GetMessagesUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteMessagesUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
import com.upsaclay.message.domain.usecase.UpdateMessageUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val BACKGROUND_SCOPE = named("BackgroundScope")

val messageDomainModule = module {
    single<CoroutineScope>(BACKGROUND_SCOPE) {
        CoroutineScope(
    SupervisorJob() +
            Dispatchers.IO +
            CoroutineExceptionHandler { coroutineContext, throwable ->
                e("Uncaught error in backgroundScope", throwable)
            }
        )
    }

    singleOf(::CreateConversationUseCase)
    singleOf(::DeleteConversationUseCase)
    singleOf(::GetConversationUIUseCase)
    singleOf(::GetConversationUseCase)
    singleOf(::GetLastMessageUseCase)
    singleOf(::GetMessagesUseCase)
    singleOf(::SendMessageUseCase)
    singleOf(::UpdateMessageUseCase)

    single {
        ListenRemoteConversationsUseCase(
            userConversationRepository = get(),
            scope = get(BACKGROUND_SCOPE)
        )
    }

    single {
        ListenRemoteMessagesUseCase(
            userConversationRepository = get(),
            messageRepository = get(),
            scope = get(BACKGROUND_SCOPE)
        )
    }
}
