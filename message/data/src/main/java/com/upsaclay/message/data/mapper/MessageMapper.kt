package com.upsaclay.message.data.mapper

import com.google.firebase.Timestamp
import com.upsaclay.message.data.local.model.LocalMessage
import com.upsaclay.message.data.remote.model.RemoteMessage
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

internal object MessageMapper {
    fun toDomain(remoteMessage: RemoteMessage) = Message(
        id = remoteMessage.messageId,
        senderId = remoteMessage.senderId,
        conversationId = remoteMessage.conversationId,
        content = remoteMessage.content,
        date = LocalDateTime.ofInstant(remoteMessage.timestamp.toInstant(), ZoneId.systemDefault()),
        isRead = remoteMessage.isRead,
        state = MessageState.SENT,
        type = remoteMessage.type
    )

    fun toDomain(localMessage: LocalMessage) = Message(
        id = localMessage.messageId,
        senderId = localMessage.senderId,
        conversationId = localMessage.conversationId,
        content = localMessage.content,
        date = Instant.ofEpochMilli(localMessage.timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime(),
        isRead = localMessage.isRead,
        state = MessageState.valueOf(localMessage.state),
        type = localMessage.type
    )

    fun toLocal(message: Message) = LocalMessage(
        messageId = message.id,
        senderId = message.senderId,
        conversationId = message.conversationId,
        content = message.content,
        timestamp = message.date.toInstant(ZoneOffset.UTC).toEpochMilli(),
        isRead = message.isRead,
        state = message.state.name,
        type = message.type
    )

    fun toRemote(message: Message) = RemoteMessage(
        messageId = message.id,
        conversationId = message.conversationId,
        senderId = message.senderId,
        content = message.content,
        timestamp = Timestamp(message.date.toInstant(ZoneOffset.UTC)),
        isRead = message.isRead,
        type = message.type
    )
}