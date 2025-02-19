package com.upsaclay.message.domain

import com.upsaclay.common.domain.e
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetConversationUserUseCase
import com.upsaclay.message.domain.usecase.GetMessagesUseCase
import com.upsaclay.message.domain.usecase.ListenConversationsUiUseCase
import com.upsaclay.message.domain.usecase.ListenConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenMessagesUseCase
import com.upsaclay.message.domain.usecase.SendMessageUseCase
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

    single {
        ListenConversationsUiUseCase(
            userConversationRepository = get(),
            messageRepository = get(),
            scope = get(BACKGROUND_SCOPE)
        )
    }

    single {
        ListenConversationsUseCase(
            userConversationRepository = get(),
            scope = get(BACKGROUND_SCOPE)
        )
    }

    single {
        ListenMessagesUseCase(
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
