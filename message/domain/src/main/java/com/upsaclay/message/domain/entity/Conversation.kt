package com.upsaclay.message.domain.entity

import com.upsaclay.common.domain.entity.User
import java.time.LocalDateTime

data class Conversation(
    val id: Int = 0,
    val interlocutor: User,
    val createdAt: LocalDateTime,
    val state: ConversationState
)