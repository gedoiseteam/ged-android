package com.upsaclay.message.domain

import com.google.gson.GsonBuilder
import com.upsaclay.common.domain.LocalDateTimeSerializer
import com.upsaclay.common.domain.UrlUtils
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.ConvertDateUseCase
import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationMessage
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.domain.entity.MessageState
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

    fun toFcmFormat(conversation: Conversation, message: Message): Map<String, String> {
        return mutableMapOf(
            "conversationId" to conversation.id.toString(),
            "interlocutorId" to conversation.interlocutor.id,
            "interlocutorFirstName" to conversation.interlocutor.firstName,
            "interlocutorLastName" to conversation.interlocutor.lastName,
            "interlocutorEmail" to conversation.interlocutor.email,
            "interlocutorSchoolLevel" to conversation.interlocutor.schoolLevel,
            "interlocutorIsMember" to conversation.interlocutor.isMember.toString(),
            "conversationCreatedAt" to conversation.createdAt.toString(),
            "messageId" to message.id.toString(),
            "senderId" to message.senderId,
            "recipientId" to message.recipientId,
            "messageContent" to message.content,
            "messageDate" to message.date.toString()
        ).apply {
            UrlUtils.getFileNameFromUrl(conversation.interlocutor.profilePictureUrl)?.let {
                put("interlocutorProfilePictureFileName", it)
            }
        }
    }

    fun fromFcmFormat(data: Map<String, String>): ConversationMessage {
        val interlocutor = User(
            id = data["interlocutorId"] ?: "",
            firstName = data["interlocutorFirstName"] ?: "",
            lastName = data["interlocutorLastName"] ?: "",
            email = data["interlocutorEmail"] ?: "",
            schoolLevel = data["interlocutorSchoolLevel"] ?: "",
            isMember = data["interlocutorIsMember"]?.toBoolean() ?: false,
            profilePictureUrl = data["interlocutorProfilePictureFileName"]?.let { UrlUtils.formatProfilePictureUrl(it) }
        )

        val conversation = Conversation(
            id = data["conversationId"]?.toInt() ?: 0,
            interlocutor = interlocutor,
            createdAt = ConvertDateUseCase.toLocalDateTime(
                data["conversationCreatedAt"]?.toLongOrNull() ?: 0L
            ),
            state = ConversationState.CREATED
        )

        val message = Message(
            id = data["messageId"]?.toInt() ?: 0,
            senderId = data["senderId"] ?: "",
            recipientId = data["recipientId"] ?: "",
            conversationId = conversation.id,
            content = data["messageContent"] ?: "",
            date = ConvertDateUseCase.toLocalDateTime(
                data["messageDate"]?.toLongOrNull() ?: 0L
            ),
            seen = null,
            state = MessageState.SENT
        )

        return ConversationMessage(conversation, message)
    }
}