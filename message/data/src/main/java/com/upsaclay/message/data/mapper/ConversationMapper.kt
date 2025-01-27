package com.upsaclay.message.data.mapper

import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.ConvertDateUseCase
import com.upsaclay.message.data.local.model.LocalConversation
import com.upsaclay.message.data.remote.model.Conversation
import com.upsaclay.message.data.remote.model.RemoteConversation
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.Message
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

internal object ConversationMapper {
    fun toLocal(conversation: Conversation, interlocutor: User) = LocalConversation(
        conversationId = conversation.id,
        interlocutorJson = Gson().toJson(interlocutor),
        createdAt = ConvertDateUseCase.toTimestamp(conversation.createdAt),
        state = conversation.state.name
    )

    fun toRemote(conversation: Conversation, currentUserId: String) = RemoteConversation(
        conversationId = conversation.id,
        participants = listOf(currentUserId, conversation.interlocutorId),
        createdAt = Timestamp(ConvertDateUseCase.toInstant(conversation.createdAt))
    )

    fun toConversationUser(conversation: Conversation, interlocutor: User) = ConversationUser(
        id = conversation.id,
        interlocutor = interlocutor,
        createdAt = conversation.createdAt,
        state = conversation.state
    )

    fun toConversationWithInterlocutor(localConversation: LocalConversation): Pair<Conversation, User> {
        val interlocutor = Gson().fromJson(localConversation.interlocutorJson, User::class.java)
        val conversation = Conversation(
            id = localConversation.conversationId,
            interlocutorId = interlocutor.id,
            createdAt = ConvertDateUseCase.toLocalDateTime(localConversation.createdAt),
            state = ConversationState.valueOf(localConversation.state)
        )
        return Pair<Conversation, User>(conversation, interlocutor)
    }

    fun toConversation(localConversation: LocalConversation): Conversation {
        val interlocutor = Gson().fromJson(localConversation.interlocutorJson, User::class.java)
        return Conversation(
            id = localConversation.conversationId,
            interlocutorId = interlocutor.id,
            createdAt = ConvertDateUseCase.toLocalDateTime(localConversation.createdAt),
            state = ConversationState.valueOf(localConversation.state)
        )
    }

    fun toConversation(remoteConversation: RemoteConversation, currentUserId: String): Conversation? {
        val interlocutorId = remoteConversation.participants.firstOrNull { it != currentUserId } ?: return null
        return Conversation(
            id = remoteConversation.conversationId,
            interlocutorId = interlocutorId,
            createdAt = ConvertDateUseCase.toLocalDateTime(remoteConversation.createdAt.toInstant()),
            state = ConversationState.CREATED
        )
    }

    fun toConversation(conversationUser: ConversationUser) = Conversation(
        id = conversationUser.id,
        interlocutorId = conversationUser.interlocutor.id,
        createdAt = conversationUser.createdAt,
        state = conversationUser.state
    )

    fun toConversationUser(conversationUI: ConversationUI) =
        ConversationUser(
            id = conversationUI.id,
            interlocutor = conversationUI.interlocutor,
            createdAt = conversationUI.createdAt,
            state = conversationUI.state
        )

    fun toConversationUI(conversationUser: ConversationUser, message: Message?) = ConversationUI(
        id = conversationUser.id,
        interlocutor = conversationUser.interlocutor,
        lastMessage = message,
        createdAt = conversationUser.createdAt,
        state = conversationUser.state
    )
}