package com.upsaclay.message.domain.usecase

import com.upsaclay.common.domain.e
import com.upsaclay.message.domain.repository.ConversationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ListenRemoteConversationsUseCase(
    private val conversationRepository: ConversationRepository,
    private val scope: CoroutineScope
) {
    internal var job: Job? = null
    private val conversations = conversationRepository.conversations

    fun start() {
        job?.cancel()
        job = scope.launch {
            conversationRepository.getRemoteConversations()
                .catch { e("Error listen remote conversation", it) }
                .filterNot { conversation ->
                    conversations.first().any { conversation == it }
                }
                .collect {
                    conversationRepository.upsertLocalConversation(it)
                }
        }
    }

    fun stop() {
        job?.cancel()
    }
}