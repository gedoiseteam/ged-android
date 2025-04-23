package com.upsaclay.message.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.upsaclay.message.data.local.model.LocalMessage
import com.upsaclay.message.data.model.MESSAGES_TABLE_NAME
import com.upsaclay.message.data.model.MessageField
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("""
        SELECT * FROM $MESSAGES_TABLE_NAME
        WHERE ${MessageField.CONVERSATION_ID} = :conversationId 
        ORDER BY ${MessageField.MESSAGE_TIMESTAMP} DESC
    """)
    fun getMessages(conversationId: Int): Flow<List<LocalMessage>>

    @Query("""
        SELECT * FROM $MESSAGES_TABLE_NAME
        WHERE ${MessageField.CONVERSATION_ID} = :conversationId
        AND (
            ${MessageField.Local.SEEN_TIMESTAMP} IS NULL 
            OR ${MessageField.Local.SEEN_VALUE} IS NULL
        )
    """)
    fun getUnreadMessages(conversationId: Int): Flow<List<LocalMessage>>
    
    @Insert
    suspend fun insertMessage(localMessage: LocalMessage)

    @Update
    suspend fun updateMessage(localMessage: LocalMessage)

    @Upsert
    suspend fun upsertMessage(localMessage: LocalMessage)

    @Query("DELETE FROM $MESSAGES_TABLE_NAME WHERE ${MessageField.CONVERSATION_ID} = :conversationId")
    suspend fun deleteMessages(conversationId: Int)

    @Query("DELETE FROM $MESSAGES_TABLE_NAME")
    suspend fun deleteAllMessages()
}