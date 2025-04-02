package com.upsaclay.message.data.model

internal object ConversationField {
    const val CONVERSATION_ID = "conversation_id"
    const val CREATED_AT = "created_at"
    const val CONVERSATION_STATE = "conversation_state"

    object Remote {
        const val PARTICIPANTS = "participants"
    }

    object Local {
        const val INTERLOCUTOR_ID = "interlocutor_id"
        const val INTERLOCUTOR_FIRST_NAME = "interlocutor_first_name"
        const val INTERLOCUTOR_LAST_NAME = "interlocutor_last_name"
        const val INTERLOCUTOR_EMAIL = "interlocutor_email"
        const val INTERLOCUTOR_SCHOOL_LEVEL = "interlocutor_school_level"
        const val INTERLOCUTOR_IS_MEMBER = "interlocutor_is_member"
        const val INTERLOCUTOR_PROFILE_PICTURE_FILE_NAME = "interlocutor_profile_picture_file_name"
    }
}

internal object MessageField {
    const val MESSAGE_ID = "message_id"
    const val CONVERSATION_ID = "conversation_id"
    const val SENDER_ID = "sender_id"
    const val RECIPIENT_ID = "recipient_id"
    const val CONTENT = "text"
    const val MESSAGE_TIMESTAMP = "message_timestamp"
    const val MESSAGE_STATE = "message_state"

    object Remote {
        const val SEEN = "seen"
        const val SEEN_VALUE = "$SEEN.value"
        const val SEEN_TIME = "$SEEN.time"
    }

    object Local {
        const val SEEN_VALUE = "seen_value"
        const val SEEN_TIMESTAMP = "seen_time"
    }
}