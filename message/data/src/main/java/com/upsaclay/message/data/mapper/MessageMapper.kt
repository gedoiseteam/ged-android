package com.upsaclay.message.data.mapper

import com.google.firebase.Timestamp
import com.upsaclay.message.data.local.model.LocalMessage
import com.upsaclay.message.data.model.MessageDTO
import com.upsaclay.message.data.remote.model.RemoteMessage
import com.upsaclay.message.domain.model.Message
import com.upsaclay.message.domain.model.MessageType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

internal object MessageMapper {
    fun toDTO(remoteMessage: RemoteMessage) = MessageDTO(
        messageId = remoteMessage.messageId,
        senderId = remoteMessage.senderId,
        conversationId = remoteMessage.conversationId,
        content = remoteMessage.content,
        date = LocalDateTime.ofInstant(remoteMessage.timestamp.toInstant(), ZoneId.systemDefault()),
        isRead = remoteMessage.isRead,
        isSent = true,
        type = remoteMessage.type
    )

    fun toDTO(localMessage: LocalMessage) = MessageDTO(
        messageId = localMessage.messageId,
        senderId = localMessage.senderId,
        conversationId = localMessage.conversationId,
        content = localMessage.content,
        date = Instant.ofEpochMilli(localMessage.timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime(),
        isRead = localMessage.isRead,
        isSent = localMessage.isSent,
        type = localMessage.type
    )

    fun toDTO(conversationId: String, message: Message, currentUserId: String) = MessageDTO(
        messageId = message.id,
        senderId = currentUserId,
        conversationId = conversationId,
        content = message.content,
        date = message.date,
        isRead = message.isRead,
        isSent = message.isSent,
        type = message.type.name
    )

    fun toDomain(messageDTO: MessageDTO, currentUserId: String) = Message(
        id = messageDTO.messageId,
        sentByUser = messageDTO.senderId == currentUserId,
        content = messageDTO.content,
        date = messageDTO.date,
        isRead = messageDTO.isRead,
        isSent = messageDTO.isSent,
        type = MessageType.valueOf(messageDTO.type.uppercase())
    )

    fun toLocal(messageDTO: MessageDTO) = LocalMessage(
        messageId = messageDTO.messageId,
        senderId = messageDTO.senderId,
        conversationId = messageDTO.conversationId,
        content = messageDTO.content,
        timestamp = messageDTO.date.toInstant(ZoneOffset.UTC).toEpochMilli(),
        isRead = messageDTO.isRead,
        isSent = messageDTO.isSent,
        type = messageDTO.type.lowercase()
    )

    fun toRemote(messageDTO: MessageDTO) = RemoteMessage(
        messageId = messageDTO.messageId,
        conversationId = messageDTO.conversationId,
        senderId = messageDTO.senderId,
        content = messageDTO.content,
        timestamp = Timestamp(messageDTO.date.toInstant(ZoneOffset.UTC)),
        isRead = messageDTO.isRead,
        type = messageDTO.type.lowercase()
    )
}