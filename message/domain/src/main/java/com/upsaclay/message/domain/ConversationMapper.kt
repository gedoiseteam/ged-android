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

    fun toConversationUI(conversationMessage: ConversationMessage): ConversationUI {
        return ConversationUI(
            id = conversationMessage.conversation.id,
            interlocutor = conversationMessage.conversation.interlocutor,
            lastMessage = conversationMessage.lastMessage,
            createdAt = conversationMessage.conversation.createdAt,
            state = conversationMessage.conversation.state
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

    fun toJson(conversationMessage: ConversationMessage): String {
        return GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
            .create()
            .toJson(conversationMessage)
    }

    fun conversationFromJson(conversationJson: String): Conversation? {
        return runCatching {
            GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
                .create()
                .fromJson(conversationJson, Conversation::class.java)
        }.getOrNull()
    }

    fun conversationMessageFromJson(conversationMessageJson: String): ConversationMessage? {
        return runCatching {
            GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
                .create()
                .fromJson(conversationMessageJson, ConversationMessage::class.java)
        }.getOrNull()
    }
}