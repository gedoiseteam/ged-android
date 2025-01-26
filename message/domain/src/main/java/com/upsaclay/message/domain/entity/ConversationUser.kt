package com.upsaclay.message.domain.entity

import com.upsaclay.common.domain.entity.User
import java.time.LocalDateTime

data class ConversationUser(
    val id: String = "",
    val interlocutor: User,
    val createdAt: LocalDateTime,
    val state: ConversationState
)