package com.upsaclay.message.domain.entity

import java.time.LocalDateTime

data class Message(
    val id: String,
    val senderId: String,
    val conversationId: String,
    val content: String,
    val date: LocalDateTime,
    val isRead: Boolean = false,
    val state: MessageState,
    val type: String = "text"
)