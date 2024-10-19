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
        text = remoteMessage.text,
        date = LocalDateTime.ofInstant(remoteMessage.timestamp.toInstant(), ZoneId.systemDefault()),
        isRead = remoteMessage.isRead,
        type = remoteMessage.type
    )

    fun toDTO(localMessage: LocalMessage) = MessageDTO(
        messageId = localMessage.messageId,
        senderId = localMessage.senderId,
        conversationId = localMessage.conversationId,
        text = localMessage.text,
        date = Instant.ofEpochMilli(localMessage.timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime(),
        isRead = localMessage.isRead,
        type = localMessage.type
    )

    fun toDomain(messageDTO: MessageDTO) = Message(
        id = messageDTO.messageId,
        senderId = messageDTO.senderId,
        text = messageDTO.text,
        date = messageDTO.date,
        isRead = messageDTO.isRead,
        type = MessageType.valueOf(messageDTO.type.uppercase())
    )

    fun toLocal(conversationId: String, message: Message) = LocalMessage(
        messageId = message.id,
        senderId = message.senderId,
        conversationId = conversationId,
        text = message.text,
        timestamp = message.date.toInstant(ZoneOffset.UTC).toEpochMilli(),
        isRead = message.isRead,
        isSent = message.isSent,
        type = message.type.name.lowercase()
    )

    fun toRemote(conversationId: String, message: Message) = RemoteMessage(
        messageId = message.id,
        conversationId = conversationId,
        senderId = message.senderId,
        text = message.text,
        timestamp = Timestamp(message.date.toInstant(ZoneOffset.UTC)),
        isRead = message.isRead,
        type = message.type.name.lowercase()
    )
}