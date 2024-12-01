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

    fun toLocal(conversationDTO: ConversationDTO) = LocalConversation(
        conversationId = conversationDTO.conversationId,
        interlocutorJson = gson.toJson(conversationDTO.interlocutor),
        isSynchronized = conversationDTO.isSynchronized,
        isActive = conversationDTO.isActive
    )

    fun toRemote(conversationDTO: ConversationDTO) = RemoteConversation(
        conversationId = conversationDTO.conversationId,
        participants = conversationDTO.participantsId,
        isActive = conversationDTO.isActive
    )

    fun toDomain(conversationDTO: ConversationDTO, messages: List<Message>) = Conversation(
        id = conversationDTO.conversationId,
        interlocutor = conversationDTO.interlocutor,
        messages = messages,
        isActive = conversationDTO.isActive
    )

    fun toDTO(localConversation: LocalConversation) = ConversationDTO(
        conversationId = localConversation.conversationId,
        interlocutor = gson.fromJson(localConversation.interlocutorJson, User::class.java),
        isSynchronized = localConversation.isSynchronized,
        participantsId = emptyList(),
        isActive = localConversation.isActive
    )

    fun toDTO(remoteConversation: RemoteConversation, interlocutor: User) = ConversationDTO(
        conversationId = remoteConversation.conversationId,
        interlocutor = interlocutor,
        isSynchronized = true,
        participantsId = remoteConversation.participants,
        isActive = true
    )

    fun toDTO(conversation: Conversation, currentUserId: String) = ConversationDTO(
        conversationId = conversation.id,
        interlocutor = conversation.interlocutor,
        isSynchronized = false,
        participantsId = listOf(currentUserId, conversation.interlocutor.id),
        isActive = conversation.isActive
    )
}