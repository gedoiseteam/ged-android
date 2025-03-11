package com.upsaclay.message.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.upsaclay.message.data.local.model.LocalConversation
import com.upsaclay.message.data.model.CONVERSATIONS_TABLE_NAME
import com.upsaclay.message.data.model.ConversationField
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM $CONVERSATIONS_TABLE_NAME")
    fun getConversations(): Flow<List<LocalConversation>>

    @Query("SELECT * FROM $CONVERSATIONS_TABLE_NAME WHERE ${ConversationField.Local.INTERLOCUTOR_ID} = :interlocutorId")
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