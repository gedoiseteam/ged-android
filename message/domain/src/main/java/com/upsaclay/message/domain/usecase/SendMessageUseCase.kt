package com.upsaclay.message.domain.usecase

import com.google.gson.GsonBuilder
import com.upsaclay.common.domain.LocalDateTimeSerializer
import com.upsaclay.common.domain.entity.FCMData
import com.upsaclay.common.domain.entity.FCMDataType
import com.upsaclay.common.domain.entity.FCMMessage
import com.upsaclay.common.domain.entity.FCMNotification
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.NotificationUseCase
import com.upsaclay.message.domain.ConversationMapper
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.repository.MessageRepository
import java.time.LocalDateTime

class SendMessageUseCase(
    private val messageRepository: MessageRepository,
    private val notificationUseCase: NotificationUseCase
) {
    suspend operator fun invoke(currentUser: User, conversation: Conversation, message: Message) {
        messageRepository.addMessage(message)
        messageRepository.upsertMessage(message)
        val fcmMessage = FCMMessage(
            recipientId = conversation.interlocutor.id,
            notification = FCMNotification(
                title = currentUser.fullName,
                body = message.content.take(100)
            ),
            data = FCMData(
                type = FCMDataType.MESSAGE,
                value = ConversationMapper.toConversationMessage(
                    conversation = conversation.copy(interlocutor = currentUser),
                    message = message
                )
            )
        )
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
            .create()
        notificationUseCase.sendNotificationToFCM(fcmMessage, gson)
    }
}