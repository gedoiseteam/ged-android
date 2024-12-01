package com.upsaclay.news.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM $ANNOUNCEMENTS_TABLE")
    fun getAllAnnouncements(): Flow<List<LocalAnnouncement>>

    @Query("SELECT * FROM $ANNOUNCEMENTS_TABLE WHERE ANNOUNCEMENT_ID = :id")
    suspend fun getAnnouncement(id: String): LocalAnnouncement?

    @Upsert
    suspend fun upsertAnnouncement(localAnnouncement: LocalAnnouncement)

    @Delete
    suspend fun deleteAnnouncement(localAnnouncement: LocalAnnouncement)
}