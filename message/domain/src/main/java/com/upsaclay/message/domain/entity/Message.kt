package com.upsaclay.message.domain.entity

import java.time.LocalDateTime

data class Message(
    val id: Int,
    val senderId: String,
    val conversationId: Int,
    val content: String,
    val date: LocalDateTime,
    val seen: Seen? = null,
    val state: MessageState,
)

data class Seen (
    val value: Boolean = true,
    val time: LocalDateTime = LocalDateTime.now()
)