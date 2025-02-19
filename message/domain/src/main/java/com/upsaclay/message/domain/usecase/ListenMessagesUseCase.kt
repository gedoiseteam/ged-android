package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ListenMessagesUseCase(
    private val userConversationRepository: UserConversationRepository,
    private val messageRepository: MessageRepository,
    private val scope: CoroutineScope
) {
    private var job: Job? = null

    fun start() {
        job?.cancel()
        job = scope.launch {
            userConversationRepository.userConversations
                .distinctUntilChanged { old, new -> old.id == new.id }
                .collect { messageRepository.listenRemoteMessages(it.id) }
        }
    }

    fun stop() {
        job?.cancel()
    }
}