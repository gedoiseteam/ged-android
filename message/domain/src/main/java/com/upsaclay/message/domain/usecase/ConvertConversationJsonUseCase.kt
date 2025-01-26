package com.upsaclay.message.domain.usecase

import com.google.gson.GsonBuilder
import com.upsaclay.common.domain.LocalDateTimeSerializer
import com.upsaclay.message.domain.entity.ConversationUI
import java.time.LocalDateTime

object ConvertConversationJsonUseCase {
    fun from(conversationJson: String): ConversationUI {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
            .create()
        return gson.fromJson(conversationJson, ConversationUI::class.java)
    }

    fun to(conversation: ConversationUI): String {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
            .create()
        return gson.toJson(conversation)
    }
}