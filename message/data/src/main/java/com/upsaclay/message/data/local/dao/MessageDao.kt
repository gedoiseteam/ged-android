package com.upsaclay.message.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.upsaclay.message.data.local.model.LocalMessage
import com.upsaclay.message.data.model.MessageField
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query(
        "SELECT * FROM messages " +
            "WHERE ${MessageField.CONVERSATION_ID} = :conversationId " +
            "ORDER BY timestamp DESC " +
            "LIMIT 10"
    )
    fun getLastMessages(conversationId: String): Flow<List<LocalMessage>>

    @Query(
        "SELECT * FROM messages " +
            "WHERE ${MessageField.CONVERSATION_ID} = :conversationId " +
            "ORDER BY timestamp DESC " +
            "LIMIT 10 " +
            "OFFSET :offset"
    )
    suspend fun getMessages(conversationId: String, offset: Int): List<LocalMessage>

    @Insert
    suspend fun insertMessage(localMessage: LocalMessage)

    @Update
    suspend fun updateMessage(localMessage: LocalMessage)
}