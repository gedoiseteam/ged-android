package com.upsaclay.message.domain.entity

sealed class ChatEvent {
    data class NewMessageReceived(val timestamp: Long) : ChatEvent()
    data class NewMessageSent(val timestamp: Long) : ChatEvent()
}