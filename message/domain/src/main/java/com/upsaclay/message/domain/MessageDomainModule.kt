package com.upsaclay.message.domain

import com.upsaclay.common.domain.e
import com.upsaclay.message.domain.usecase.CreateConversationUseCase
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetFilteredUserUseCase
import com.upsaclay.message.domain.usecase.GetPagedConversationsUIUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteMessagesUseCase
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
            CoroutineExceptionHandler { _, throwable ->
                e("Uncaught error in backgroundScope", throwable)
            }
        )
    }

    singleOf(::CreateConversationUseCase)
    singleOf(::DeleteConversationUseCase)
    singleOf(::GetPagedConversationsUIUseCase)
    singleOf(::GetFilteredUserUseCase)
    singleOf(::SendMessageUseCase)

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
