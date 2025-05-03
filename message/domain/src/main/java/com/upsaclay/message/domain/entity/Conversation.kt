package com.upsaclay.message.domain.entity

import com.upsaclay.common.domain.LocalDateTimeSerializer
import com.upsaclay.common.domain.entity.User
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Conversation(
    val id: Int,
    val interlocutor: User,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val state: ConversationState
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Conversation) return false

        return id == other.id &&
                interlocutor == other.interlocutor &&
                createdAt.withNano(0) == other.createdAt.withNano(0) &&
                state == other.state
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + interlocutor.hashCode()
        result = 31 * result + createdAt.withNano(0).hashCode()
        result = 31 * result + state.hashCode()
        return result
    }
}

enum class ConversationState {
    DEFAULT,
    LOADING,
    CREATED,
    NOT_CREATED
}