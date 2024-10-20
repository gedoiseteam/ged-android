package com.upsaclay.message.data.model

import com.upsaclay.common.domain.model.User

data class ConversationDTO(
    val conversationId: String,
    val interlocutor: User,
    val isSynchronized: Boolean,
    val participantsId: List<Int>,
    val isActive: Boolean
)
