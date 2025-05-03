package com.upsaclay.message.domain.usecase

import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.ConversationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ListenRemoteMessagesUseCase(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val scope: CoroutineScope
) {
    internal var job: Job? = null
    private var listeningScope: CoroutineScope? = null

    fun start() {
        job?.cancel()
        job = conversationRepository.conversations
            .onEach { conversations ->
                listeningScope?.cancel()
                listeningScope = CoroutineScope(scope.coroutineContext + SupervisorJob())
                conversations.forEach {
                    listeningScope?.launch {
                        messageRepository.listenRemoteMessages(it.id)
                    }
                }
            }.launchIn(scope)
    }

    fun stop() {
        job?.cancel()
        listeningScope?.cancel()
    }
}