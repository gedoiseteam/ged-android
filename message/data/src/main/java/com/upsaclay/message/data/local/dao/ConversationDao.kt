package com.upsaclay.message.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.upsaclay.message.data.local.model.LocalConversation
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations")
    fun getAllConversationsFlow(): Flow<List<LocalConversation>>

    @Query("SELECT * FROM conversations")
    fun getAllConversations(): List<LocalConversation>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertConversation(localConversation: LocalConversation)

    @Update
    suspend fun updateConversation(localConversation: LocalConversation)

    @Delete
    suspend fun deleteConversation(localConversation: LocalConversation)
}