package com.upsaclay.message.domain

import com.upsaclay.message.domain.entity.Conversation
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.Message

internal object ConversationMapper {
    fun toConversationUser(conversationUI: ConversationUI) = Conversation(
        id = conversationUI.id,
        interlocutor = conversationUI.interlocutor,
        createdAt = conversationUI.createdAt,
        state = conversationUI.state
    )

    fun toConversationUI(conversation: Conversation, message: Message?) =
        ConversationUI(
            id = conversation.id,
            interlocutor = conversation.interlocutor,
            lastMessage = message,
            createdAt = conversation.createdAt,
            state = conversation.state
        )
}