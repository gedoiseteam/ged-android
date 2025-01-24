package com.upsaclay.message.data

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
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val messageDataModule = module {
    singleOf(::ConversationRepositoryImpl) { bind<ConversationRepository>() }
    singleOf(::ConversationApiImpl) { bind<ConversationApi>() }
    singleOf(::ConversationRemoteDataSource)
    singleOf(::ConversationLocalDataSource)

    singleOf(::MessageRepositoryImpl) { bind<MessageRepository>() }
    singleOf(::MessageApiImpl) { bind<MessageApi>() }
    singleOf(::MessageRemoteDataSource)
    singleOf(::MessageLocalDataSource)

    singleOf(::UserConversationRepositoryImpl) { bind<UserConversationRepository>() }
}