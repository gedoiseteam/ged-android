package com.upsaclay.message.domain.usecase

import com.google.gson.GsonBuilder
import com.upsaclay.common.domain.LocalDateTimeSerializer
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationUI
import java.time.LocalDateTime

object ConvertConversationJsonUseCase {
    operator fun invoke(conversation: ConversationUI): String {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
            .create()
        return gson.toJson(conversation)
    }

    operator fun invoke(conversation: Conversation): String {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
            .create()
        return gson.toJson(conversation)
    }

    fun from(conversationJson: String): ConversationUI {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
            .create()
        return gson.fromJson(conversationJson, ConversationUI::class.java)
    }
}