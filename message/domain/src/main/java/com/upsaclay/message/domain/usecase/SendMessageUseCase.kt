package com.upsaclay.message.domain.usecase

import com.upsaclay.common.domain.repository.FCMRepository
import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.repository.MessageRepository

class SendMessageUseCase(
    private val messageRepository: MessageRepository,
    private val fcmRepository: FCMRepository
) {
    suspend operator fun invoke(conversation: Conversation, message: Message) {
        messageRepository.addMessage(message)
        messageRepository.upsertMessage(message)
        val notificationMessageData = ConversationMapper.toFcmFormat(conversation, message)
        fcmRepository.sendNotification(message.senderId, notificationMessageData)
    }
}