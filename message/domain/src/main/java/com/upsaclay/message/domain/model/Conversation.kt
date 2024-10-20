package com.upsaclay.message.domain.model

import com.upsaclay.common.domain.model.User

data class Conversation(
    val id: String = "",
    val interlocutor: User,
    val messages: List<Message>,
    val isActive: Boolean = false
)