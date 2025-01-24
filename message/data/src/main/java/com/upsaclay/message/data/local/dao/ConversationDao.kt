package com.upsaclay.message.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.upsaclay.message.data.local.model.LocalConversation
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations")
    fun getConversations(): Flow<List<LocalConversation>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertConversation(localConversation: LocalConversation)

    @Update
    suspend fun updateConversation(localConversation: LocalConversation)

    @Upsert
    suspend fun upsertConversation(localConversation: LocalConversation)

    @Delete
    suspend fun deleteConversation(localConversation: LocalConversation)
}