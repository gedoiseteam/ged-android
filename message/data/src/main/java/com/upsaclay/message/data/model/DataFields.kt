package com.upsaclay.message.data.model

internal object ConversationField {
    const val CONVERSATION_ID = "conversation_id"
    const val IS_ACTIVE = "is_active"

    object Remote {
        const val PARTICIPANTS = "participants"
    }

    object Local {
        const val IS_SYNCHRONIZED = "is_synchronized"
        const val INTERLOCUTOR = "interlocutor"
    }
}

internal object MessageField {
    const val MESSAGE_ID = "message_id"
    const val CONVERSATION_ID = "conversation_id"
    const val SENDER_ID = "sender_id"
    const val CONTENT = "text"
    const val TIMESTAMP = "timestamp"
    const val IS_READ = "is_read"
    const val TYPE = "type"

    object Local {
        const val IS_SENT = "is_sent"
    }
}