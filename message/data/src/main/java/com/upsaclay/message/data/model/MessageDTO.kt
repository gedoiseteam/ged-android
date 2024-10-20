package com.upsaclay.message.data.model

import java.time.LocalDateTime

internal data class MessageDTO(
    val messageId: String,
    val senderId: Int,
    val conversationId: String,
    val content: String,
    val date: LocalDateTime,
    val isRead: Boolean,
    val isSent: Boolean,
    val type: String
)