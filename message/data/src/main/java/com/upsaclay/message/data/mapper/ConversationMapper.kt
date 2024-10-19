package com.upsaclay.message.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.upsaclay.common.domain.model.User
import com.upsaclay.message.data.local.model.LocalConversation
import com.upsaclay.message.data.model.ConversationDTO
import com.upsaclay.message.data.remote.model.RemoteConversation
import com.upsaclay.message.domain.model.Conversation
import com.upsaclay.message.domain.model.Message

internal object ConversationMapper {
    private val gson = Gson()

    fun toDTO(localConversation: LocalConversation): ConversationDTO {
        val type = object : TypeToken<List<Int>>() {}.type
        return ConversationDTO(
            conversationId = localConversation.conversationId,
            participants = gson.fromJson(localConversation.usersIdsJson, type)
        )
    }

    fun toDTO(conversation: Conversation, participants: List<Int>) = ConversationDTO(
        conversationId = conversation.id,
        participants = participants
    )

    fun toLocal(conversationDTO: ConversationDTO) = LocalConversation(
        conversationId = conversationDTO.conversationId,
        usersIdsJson = gson.toJson(conversationDTO.participants)
    )

    fun toLocal(remoteConversation: RemoteConversation) = LocalConversation(
        conversationId = remoteConversation.conversationId,
        usersIdsJson = gson.toJson(remoteConversation.participants)
    )

    fun toRemote(conversationDTO: ConversationDTO) = RemoteConversation(
        conversationId = conversationDTO.conversationId,
        participants = conversationDTO.participants
    )

    fun toDomain(conversationId: String, interlocutor: User, messages: List<Message>) = Conversation(
        id = conversationId,
        interlocutor = interlocutor,
        messages = messages
    )
}