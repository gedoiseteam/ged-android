package com.upsaclay.message.data.remote.model

import com.upsaclay.message.domain.entity.ConversationState
import java.time.LocalDateTime

internal data class Conversation(
    val id: String,
    val interlocutorId: String,
    val createdAt: LocalDateTime,
    val state: ConversationState
)