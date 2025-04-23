package com.upsaclay.message.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.upsaclay.message.data.local.model.LocalConversation
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
import com.upsaclay.message.data.model.MessageField.RECIPIENT_ID
import com.upsaclay.message.data.model.MessageField.SENDER_ID
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM $CONVERSATIONS_TABLE_NAME")
    fun getConversations(): Flow<List<LocalConversation>>

    @Query("SELECT * FROM $CONVERSATIONS_TABLE_NAME WHERE $INTERLOCUTOR_ID = :interlocutorId")
    suspend fun getConversation(interlocutorId: String): LocalConversation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(localConversation: LocalConversation)

    @Update
    suspend fun updateConversation(localConversation: LocalConversation)

    @Upsert
    suspend fun upsertConversation(localConversation: LocalConversation)

    @Delete
    suspend fun deleteConversation(localConversation: LocalConversation)

    @Query("DELETE FROM $CONVERSATIONS_TABLE_NAME")
    suspend fun deleteConversations()
}