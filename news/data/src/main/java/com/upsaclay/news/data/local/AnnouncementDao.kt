package com.upsaclay.news.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.upsaclay.news.data.model.ANNOUNCEMENTS_TABLE
import com.upsaclay.news.data.model.LocalAnnouncement
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM $ANNOUNCEMENTS_TABLE")
    fun getAnnouncements(): Flow<List<LocalAnnouncement>>

    @Insert
    suspend fun insertAnnouncement(localAnnouncement: LocalAnnouncement)

    @Update
    suspend fun updateAnnouncement(localAnnouncement: LocalAnnouncement)

    @Upsert
    suspend fun upsertAnnouncement(localAnnouncement: LocalAnnouncement)

    @Delete
    suspend fun deleteAnnouncement(localAnnouncement: LocalAnnouncement)
}