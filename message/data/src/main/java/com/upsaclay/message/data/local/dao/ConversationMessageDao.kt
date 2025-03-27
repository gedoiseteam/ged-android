package com.upsaclay.message.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.upsaclay.message.data.local.model.LocalConversationMessage
import com.upsaclay.message.data.model.CONVERSATIONS_TABLE_NAME
import com.upsaclay.message.data.model.ConversationField
import com.upsaclay.message.data.model.ConversationField.CONVERSATION_STATE
import com.upsaclay.message.data.model.ConversationField.CREATED_AT
import com.upsaclay.message.data.model.ConversationField.Local.INTERLOCUTOR_EMAIL
import com.upsaclay.message.data.model.ConversationField.Local.INTERLOCUTOR_FIRST_NAME
import com.upsaclay.message.data.model.ConversationField.Local.INTERLOCUTOR_ID
import com.upsaclay.message.data.model.ConversationField.Local.INTERLOCUTOR_IS_MEMBER
import com.upsaclay.message.data.model.ConversationField.Local.INTERLOCUTOR_LAST_NAME
import com.upsaclay.message.data.model.ConversationField.Local.INTERLOCUTOR_PROFILE_PICTURE_FILE_NAME
import com.upsaclay.message.data.model.ConversationField.Local.INTERLOCUTOR_SCHOOL_LEVEL
import com.upsaclay.message.data.model.MESSAGES_TABLE_NAME
import com.upsaclay.message.data.model.MessageField
import com.upsaclay.message.data.model.MessageField.CONTENT
import com.upsaclay.message.data.model.MessageField.Local.SEEN_TIMESTAMP
import com.upsaclay.message.data.model.MessageField.Local.SEEN_VALUE
import com.upsaclay.message.data.model.MessageField.MESSAGE_ID
import com.upsaclay.message.data.model.MessageField.MESSAGE_STATE
import com.upsaclay.message.data.model.MessageField.MESSAGE_TIMESTAMP
import com.upsaclay.message.data.model.MessageField.SENDER_ID
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationMessageDao {
    @Transaction
    @Query("""
        SELECT C.${ConversationField.CONVERSATION_ID}, 
           C.$INTERLOCUTOR_ID, 
           C.$INTERLOCUTOR_FIRST_NAME,
           C.$INTERLOCUTOR_LAST_NAME, 
           C.$INTERLOCUTOR_EMAIL, 
           C.$INTERLOCUTOR_SCHOOL_LEVEL,
           C.$INTERLOCUTOR_IS_MEMBER,
           C.$INTERLOCUTOR_PROFILE_PICTURE_FILE_NAME,
           C.$CREATED_AT,
           C.$CONVERSATION_STATE, 
           M.$MESSAGE_ID, 
           M.$SENDER_ID,
           M.$CONTENT,
           M.$MESSAGE_TIMESTAMP,
           M.$SEEN_VALUE, 
           M.$SEEN_TIMESTAMP,
           M.$MESSAGE_STATE
        FROM $CONVERSATIONS_TABLE_NAME C
        JOIN $MESSAGES_TABLE_NAME M ON C.${ConversationField.CONVERSATION_ID} = M.${MessageField.CONVERSATION_ID}
        JOIN (
            SELECT ${MessageField.CONVERSATION_ID}, MAX($MESSAGE_TIMESTAMP) AS MAX_TIMESTAMP
            FROM $MESSAGES_TABLE_NAME
            GROUP BY ${MessageField.CONVERSATION_ID}
        ) M_MAX
          ON M.${MessageField.CONVERSATION_ID} = M_MAX.${MessageField.CONVERSATION_ID}
          AND M.$MESSAGE_TIMESTAMP = M_MAX.MAX_TIMESTAMP
          ORDER BY C.$CREATED_AT DESC
    """)
    fun getPagedConversationsWithLastMessage(): PagingSource<Int, LocalConversationMessage>

    @Transaction
    @Query("""
        SELECT C.${ConversationField.CONVERSATION_ID}, 
           C.$INTERLOCUTOR_ID, 
           C.$INTERLOCUTOR_FIRST_NAME,
           C.$INTERLOCUTOR_LAST_NAME, 
           C.$INTERLOCUTOR_EMAIL, 
           C.$INTERLOCUTOR_SCHOOL_LEVEL,
           C.$INTERLOCUTOR_IS_MEMBER,
           C.$INTERLOCUTOR_PROFILE_PICTURE_FILE_NAME,
           C.$CREATED_AT,
           C.$CONVERSATION_STATE, 
           M.$MESSAGE_ID, 
           M.$SENDER_ID,
           M.$CONTENT,
           M.$MESSAGE_TIMESTAMP,
           M.$SEEN_VALUE, 
           M.$SEEN_TIMESTAMP,
           M.$MESSAGE_STATE
        FROM $CONVERSATIONS_TABLE_NAME C
        JOIN $MESSAGES_TABLE_NAME M ON C.${ConversationField.CONVERSATION_ID} = M.${MessageField.CONVERSATION_ID}
        JOIN (
            SELECT ${MessageField.CONVERSATION_ID}, MAX($MESSAGE_TIMESTAMP) AS MAX_TIMESTAMP
            FROM $MESSAGES_TABLE_NAME
            GROUP BY ${MessageField.CONVERSATION_ID}
        ) M_MAX
          ON M.${MessageField.CONVERSATION_ID} = M_MAX.${MessageField.CONVERSATION_ID}
          AND M.$MESSAGE_TIMESTAMP = M_MAX.MAX_TIMESTAMP
          ORDER BY C.$CREATED_AT DESC
    """)
    fun getConversationWithLastMessages(): Flow<List<LocalConversationMessage>>
}