package com.upsaclay.message.domain.usecase

import com.google.gson.GsonBuilder
import com.upsaclay.common.domain.LocalDateTimeSerializer
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.ConversationUser
import java.time.LocalDateTime

object ConvertConversationJsonUseCase {
    operator fun invoke(conversation: ConversationUser): String {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
            .create()
        return gson.toJson(conversation)
    }

    fun from(conversationJson: String): ConversationUser {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
            .create()
        return gson.fromJson(conversationJson, ConversationUser::class.java)
    }
}