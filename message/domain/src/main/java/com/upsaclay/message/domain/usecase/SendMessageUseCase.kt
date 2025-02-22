package com.upsaclay.message.domain.usecase

import com.upsaclay.common.domain.e
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SendMessageUseCase(
    private val messageRepository: MessageRepository,
    private val scope: CoroutineScope
) {
    operator fun invoke(message: Message) {
        scope.launch {
            try {
                messageRepository.createMessage(message)
            } catch (e: Exception) {
                e("Error sending message: $message", e)
            }
        }
    }
}