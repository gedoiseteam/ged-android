package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.repository.UserConversationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ListenConversationsUseCase(
    private val userConversationRepository: UserConversationRepository,
    private val scope: CoroutineScope
) {
    internal var job: Job? = null

    fun start() {
        job?.cancel()
        job = scope.launch {
            launch { userConversationRepository.listenLocalConversations() }
            launch { userConversationRepository.listenRemoteConversations() }
        }
    }

    fun stop() {
        job?.cancel()
    }
}