package com.upsaclay.message.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.upsaclay.message.data.local.model.LocalConversationMessage
import com.upsaclay.message.data.model.CONVERSATIONS_TABLE_NAME
import com.upsaclay.message.data.model.ConversationField
import com.upsaclay.message.data.model.MESSAGES_TABLE_NAME
import com.upsaclay.message.data.model.MessageField

@Dao
interface ConversationMessageDao {
    @Transaction
    @Query("""
        SELECT C.${ConversationField.CONVERSATION_ID}, 
            C.${ConversationField.Local.INTERLOCUTOR_ID}, 
            C.${ConversationField.Local.INTERLOCUTOR_FIRST_NAME},
            C.${ConversationField.Local.INTERLOCUTOR_LAST_NAME}, 
            C.${ConversationField.Local.INTERLOCUTOR_EMAIL}, 
            C.${ConversationField.Local.INTERLOCUTOR_SCHOOL_LEVEL},
            C.${ConversationField.Local.INTERLOCUTOR_IS_MEMBER},
            C.${ConversationField.Local.INTERLOCUTOR_PROFILE_PICTURE_FILE_NAME},
            C.${ConversationField.CREATED_AT},
            C.${ConversationField.CONVERSATION_STATE}, 
            M.${MessageField.MESSAGE_ID}, 
            M.${MessageField.SENDER_ID},
            M.${MessageField.CONTENT},
            M.${MessageField.MESSAGE_TIMESTAMP},
            M.${MessageField.Local.SEEN_VALUE}, 
            M.${MessageField.Local.SEEN_TIME},
            M.${MessageField.MESSAGE_STATE}
        FROM $CONVERSATIONS_TABLE_NAME C
        JOIN $MESSAGES_TABLE_NAME M ON C.${ConversationField.CONVERSATION_ID} = M.${MessageField.CONVERSATION_ID}
        WHERE M.${MessageField.MESSAGE_TIMESTAMP} = (
            SELECT MAX(${MessageField.MESSAGE_TIMESTAMP})
            FROM $MESSAGES_TABLE_NAME
            WHERE ${MessageField.CONVERSATION_ID} = C.${ConversationField.CONVERSATION_ID}
        )
    """)
    fun getConversationsWithLastMessage(): PagingSource<Int, LocalConversationMessage>
}