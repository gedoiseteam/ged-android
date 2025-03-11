package com.upsaclay.message.data.mapper

import com.google.firebase.Timestamp
import com.upsaclay.common.domain.usecase.ConvertDateUseCase
import com.upsaclay.message.data.local.model.LocalMessage
import com.upsaclay.message.data.remote.model.RemoteMessage
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
import com.upsaclay.message.domain.entity.Seen
import java.time.Instant
import java.time.ZoneOffset

internal object MessageMapper {
    fun toDomain(remoteMessage: RemoteMessage) = Message(
        id = remoteMessage.messageId,
        senderId = remoteMessage.senderId,
        conversationId = remoteMessage.conversationId,
        content = remoteMessage.content,
        date = ConvertDateUseCase.toLocalDateTime(remoteMessage.timestamp.toInstant()),
        seen = remoteMessage.seen,
        state = MessageState.SENT
    )

    fun toDomain(localMessage: LocalMessage) = Message(
        id = localMessage.messageId,
        senderId = localMessage.senderId,
        conversationId = localMessage.conversationId,
        content = localMessage.content,
        date = Instant.ofEpochMilli(localMessage.messageTimestamp).atZone(ZoneOffset.UTC).toLocalDateTime(),
        seen = if (localMessage.seenValue == null || localMessage.seenTimestamp == null) {
            null
        } else {
            Seen(
                value = localMessage.seenValue,
                time = ConvertDateUseCase.toLocalDateTime(localMessage.seenTimestamp)
            )
        },
        state = MessageState.valueOf(localMessage.state)
    )

    fun toLocal(message: Message) = LocalMessage(
        messageId = message.id,
        senderId = message.senderId,
        conversationId = message.conversationId,
        content = message.content,
        messageTimestamp = message.date.toInstant(ZoneOffset.UTC).toEpochMilli(),
        seenValue = message.seen?.value,
        seenTimestamp = message.seen?.time?.let { ConvertDateUseCase.toTimestamp(it) },
        state = message.state.name
    )

    fun toRemote(message: Message) = RemoteMessage(
        messageId = message.id,
        conversationId = message.conversationId,
        senderId = message.senderId,
        content = message.content,
        timestamp = Timestamp(message.date.atZone(ZoneOffset.UTC).toInstant()),
        seen = message.seen
    )
}