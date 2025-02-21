package com.upsaclay.message.data.model

internal object ConversationField {
    const val CONVERSATION_ID = "conversation_id"
    const val CREATED_AT = "created_at"
    const val STATE = "state"

    object Remote {
        const val PARTICIPANTS = "participants"
    }

    object Local {
        const val INTERLOCUTOR = "interlocutor"
    }
}

internal object MessageField {
    const val MESSAGE_ID = "message_id"
    const val CONVERSATION_ID = "conversation_id"
    const val SENDER_ID = "sender_id"
    const val CONTENT = "text"
    const val TIMESTAMP = "timestamp"
    const val SEEN = "seen"
    const val TYPE = "type"
    const val STATE = "state"
}