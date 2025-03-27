package com.upsaclay.message.domain.entity

data class ConversationMessage(
    val conversation: Conversation,
    val lastMessage: Message?
)