package com.upsaclay.message.data

import com.upsaclay.message.data.local.ConversationLocalDataSource
import com.upsaclay.message.data.local.ConversationMessageLocalDataSource
import com.upsaclay.message.data.local.MessageLocalDataSource
import com.upsaclay.message.data.remote.ConversationRemoteDataSource
import com.upsaclay.message.data.remote.MessageRemoteDataSource
import com.upsaclay.message.data.remote.api.ConversationApi
import com.upsaclay.message.data.remote.api.ConversationApiImpl
import com.upsaclay.message.data.remote.api.MessageApi
import com.upsaclay.message.data.remote.api.MessageApiImpl
import com.upsaclay.message.domain.repository.ConversationMessageRepository
import com.upsaclay.message.data.repository.ConversationMessageRepositoryImpl
import com.upsaclay.message.data.repository.MessageRepositoryImpl
import com.upsaclay.message.data.repository.ConversationRepositoryImpl
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.ConversationRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val messageDataModule = module {
    singleOf(::ConversationApiImpl) { bind<ConversationApi>() }
    singleOf(::ConversationRemoteDataSource)
    singleOf(::ConversationLocalDataSource)

    singleOf(::ConversationRepositoryImpl) { bind<com.upsaclay.message.domain.repository.ConversationRepository>() }

    singleOf(::ConversationMessageRepositoryImpl) { bind<ConversationMessageRepository>() }
    singleOf(::ConversationMessageLocalDataSource)

    singleOf(::MessageRepositoryImpl) { bind<MessageRepository>() }
    singleOf(::MessageApiImpl) { bind<MessageApi>() }
    singleOf(::MessageRemoteDataSource)
    singleOf(::MessageLocalDataSource)
}