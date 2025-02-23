package com.upsaclay.message.data.mapper

import com.google.firebase.Timestamp
import com.upsaclay.message.data.local.model.LocalMessage
import com.upsaclay.message.data.remote.model.RemoteMessage
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

internal object MessageMapper {
    fun toDomain(remoteMessage: RemoteMessage) = Message(
        id = remoteMessage.messageId,
        senderId = remoteMessage.senderId,
        conversationId = remoteMessage.conversationId,
        content = remoteMessage.content,
        date = LocalDateTime.ofInstant(remoteMessage.timestamp.toInstant(), ZoneOffset.UTC),
        seen = remoteMessage.seen,
        state = MessageState.SENT,
        type = remoteMessage.type
    )

    fun toDomain(localMessage: LocalMessage) = Message(
        id = localMessage.messageId,
        senderId = localMessage.senderId,
        conversationId = localMessage.conversationId,
        content = localMessage.content,
        date = Instant.ofEpochMilli(localMessage.timestamp).atZone(ZoneOffset.UTC).toLocalDateTime(),
        seen = localMessage.seen,
        state = MessageState.valueOf(localMessage.state),
        type = localMessage.type
    )

    fun toLocal(message: Message) = LocalMessage(
        messageId = message.id,
        senderId = message.senderId,
        conversationId = message.conversationId,
        content = message.content,
        timestamp = message.date.toInstant(ZoneOffset.UTC).toEpochMilli(),
        seen = message.seen,
        state = message.state.name,
        type = message.type
    )

    fun toRemote(message: Message) = RemoteMessage(
        messageId = message.id,
        conversationId = message.conversationId,
        senderId = message.senderId,
        content = message.content,
        timestamp = Timestamp(message.date.atZone(ZoneOffset.UTC).toInstant()),
        seen = message.seen,
        type = message.type
    )
}