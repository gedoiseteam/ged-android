package com.upsaclay.message.domain

import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.ConversationUser
import com.upsaclay.message.domain.entity.Message

object ConversationMapper {
    fun toConversationUser(conversationUI: ConversationUI) = ConversationUser(
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