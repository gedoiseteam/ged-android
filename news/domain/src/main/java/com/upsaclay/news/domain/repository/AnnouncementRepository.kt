package com.upsaclay.news.domain.repository

import com.upsaclay.news.domain.entity.Announcement
import kotlinx.coroutines.flow.Flow

interface AnnouncementRepository {
    val announcements: Flow<List<Announcement>>

    fun getAnnouncement(announcementId: String): Announcement?

    suspend fun refreshAnnouncements()

    suspend fun createAnnouncement(announcement: Announcement)

    suspend fun updateAnnouncement(announcement: Announcement)

    suspend fun deleteAnnouncement(announcement: Announcement)
}