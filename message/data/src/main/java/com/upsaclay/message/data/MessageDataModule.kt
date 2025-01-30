package com.upsaclay.message.data

import com.upsaclay.common.domain.e
import com.upsaclay.message.data.local.ConversationLocalDataSource
import com.upsaclay.message.data.local.MessageLocalDataSource
import com.upsaclay.message.data.remote.ConversationRemoteDataSource
import com.upsaclay.message.data.remote.MessageRemoteDataSource
import com.upsaclay.message.data.remote.api.ConversationApi
import com.upsaclay.message.data.remote.api.ConversationApiImpl
import com.upsaclay.message.data.remote.api.MessageApi
import com.upsaclay.message.data.remote.api.MessageApiImpl
import com.upsaclay.message.data.repository.ConversationRepository
import com.upsaclay.message.data.repository.ConversationRepositoryImpl
import com.upsaclay.message.data.repository.MessageRepositoryImpl
import com.upsaclay.message.data.repository.UserConversationRepositoryImpl
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val BACKGROUND_SCOPE = named("BackgroundScope")

val messageDataModule = module {
    single<CoroutineScope>(BACKGROUND_SCOPE) {
        CoroutineScope(
    SupervisorJob() +
            Dispatchers.IO +
            CoroutineExceptionHandler { coroutineContext, throwable ->
                e("Uncaught error in backgroundScope", throwable)
            }
        )
    }

    singleOf(::ConversationRepositoryImpl) { bind<ConversationRepository>() }
    singleOf(::ConversationApiImpl) { bind<ConversationApi>() }
    singleOf(::ConversationRemoteDataSource)
    singleOf(::ConversationLocalDataSource)

    single<MessageRepository> {
        MessageRepositoryImpl(
            messageRemoteDataSource = get(),
            messageLocalDataSource = get(),
            scope = get(BACKGROUND_SCOPE)
        )
    }
    singleOf(::MessageApiImpl) { bind<MessageApi>() }
    singleOf(::MessageRemoteDataSource)
    singleOf(::MessageLocalDataSource)

    single<UserConversationRepository> {
        UserConversationRepositoryImpl(
            conversationRepository = get(),
            userRepository = get(),
            scope = get(BACKGROUND_SCOPE)
        )
    }
}