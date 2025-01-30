package com.upsaclay.message.data.local.dao

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
    @Query(
        "SELECT * FROM $MESSAGES_TABLE_NAME " +
            "WHERE ${MessageField.CONVERSATION_ID} = :conversationId " +
            "ORDER BY timestamp DESC " +
            "LIMIT 1"
    )
    fun getLastMessage(conversationId: String): Flow<LocalMessage?>

    @Query(
        "SELECT * FROM $MESSAGES_TABLE_NAME " +
            "WHERE ${MessageField.CONVERSATION_ID} = :conversationId " +
            "ORDER BY timestamp DESC " +
            "LIMIT 10 " +
            "OFFSET :offset"
    )
    fun getMessages(conversationId: String, offset: Int): Flow<List<LocalMessage>>

    @Insert
    suspend fun insertMessage(localMessage: LocalMessage)

    @Update
    suspend fun updateMessage(localMessage: LocalMessage)

    @Upsert
    suspend fun upsertMessage(localMessage: LocalMessage)

    @Query("DELETE FROM $MESSAGES_TABLE_NAME")
    suspend fun deleteMessages()
}