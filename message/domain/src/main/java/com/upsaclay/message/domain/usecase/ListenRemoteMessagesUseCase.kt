package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class ListenRemoteMessagesUseCase(
    private val userConversationRepository: UserConversationRepository,
    private val messageRepository: MessageRepository,
    private val scope: CoroutineScope
) {
    internal var job: Job? = null

    fun start() {
        job?.cancel()
        job = userConversationRepository.getConversations().mapLatest { userConversations ->
            userConversations.forEach {
                messageRepository.listenRemoteMessages(it.id)
            }
        }.launchIn(scope)
    }

    fun stop() {
        job?.cancel()
    }
}