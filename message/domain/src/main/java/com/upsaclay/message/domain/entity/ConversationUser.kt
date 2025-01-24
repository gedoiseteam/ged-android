package com.upsaclay.message.domain.entity

import com.upsaclay.common.domain.model.User
import java.time.LocalDateTime

internal data class ConversationUser(
    val id: String = "",
    val interlocutor: User,
    val createdAt: LocalDateTime,
    val state: ConversationState
)