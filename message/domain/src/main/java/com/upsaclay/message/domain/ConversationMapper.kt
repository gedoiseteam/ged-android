package com.upsaclay.message.domain

import com.google.gson.GsonBuilder
import com.upsaclay.common.domain.LocalDateTimeSerializer
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationMessage
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.Message
import java.time.LocalDateTime

object ConversationMapper {
    fun toConversation(conversationUI: ConversationUI) = Conversation(
        id = conversationUI.id,
        interlocutor = conversationUI.interlocutor,
        createdAt = conversationUI.createdAt,
        state = conversationUI.state
    )

    fun toConversationUI(conversation: Conversation, message: Message?): ConversationUI {
        return ConversationUI(
            id = conversation.id,
            interlocutor = conversation.interlocutor,
            lastMessage = message,
            createdAt = conversation.createdAt,
            state = conversation.state
        )
    }

    fun toConversationMessage(conversation: Conversation, message: Message): ConversationMessage {
        return ConversationMessage(
            conversation = conversation,
            lastMessage = message
        )
    }

    fun toJson(conversation: Conversation): String {
        return GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
            .create()
            .toJson(conversation)
    }

    fun fromJson(conversationJson: String): Conversation {
        return GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
            .create()
            .fromJson(conversationJson, Conversation::class.java)
    }
}