package com.upsaclay.message.domain.repository

import com.upsaclay.message.domain.model.Message

interface MessageRepository {
    suspend fun sendMessage(
        conversationId: String,
        message: Message,
        currentUserId: String
    ): Result<Unit>
}