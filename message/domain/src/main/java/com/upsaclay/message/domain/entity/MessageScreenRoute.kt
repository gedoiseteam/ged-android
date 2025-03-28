package com.upsaclay.message.domain.entity

import com.upsaclay.common.domain.entity.ScreenRoute
import com.upsaclay.message.domain.ConversationMapper

private const val CHAT_ROUTE = "chat_screen"
private const val CONVERSATION_ROUTE = "conversation_screen"
private const val CREATE_CONVERSATION_ROUTE = "create_conversation_screen"

typealias ChatConversation = Conversation

sealed class MessageScreenRoute: ScreenRoute {
    data class Chat(val conversation: ChatConversation): MessageScreenRoute() {
        override val route: String = "$CHAT_ROUTE?conversation=${ConversationMapper.toJson(conversation)}"
        companion object {
            const val HARD_ROUTE = "$CHAT_ROUTE?conversation={conversation}"
        }
    }

    data object Conversation: MessageScreenRoute() {
        override val route: String = CONVERSATION_ROUTE
    }

    data object CreateConversation: MessageScreenRoute() {
        override val route: String = CREATE_CONVERSATION_ROUTE
    }
}