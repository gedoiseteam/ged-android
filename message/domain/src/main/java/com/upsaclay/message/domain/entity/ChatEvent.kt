package com.upsaclay.message.domain.entity

sealed class ChatEvent {
    data class NewMessage(val message: Message) : ChatEvent()
}