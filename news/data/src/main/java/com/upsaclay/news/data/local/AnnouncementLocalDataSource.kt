package com.upsaclay.news.data.local

import com.upsaclay.news.domain.model.Announcement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class AnnouncementLocalDataSource(private val announcementDao: AnnouncementDao) {
    suspend fun getAllAnnouncements(): Flow<List<Announcement>> = withContext(Dispatchers.IO) {
        announcementDao.getAllAnnouncements().map { announcementLocals ->
            announcementLocals.map { it.toDomain() }
        }
    }

    suspend fun getAnnouncement(id: String): Announcement? = withContext(Dispatchers.IO) {
        announcementDao.getAnnouncement(id)?.toDomain()
    }

    suspend fun upsertAnnouncement(announcement: Announcement) {
        withContext(Dispatchers.IO) {
            val localAnnouncement = LocalAnnouncement.fromDomain(announcement)
            announcementDao.upsertAnnouncement(localAnnouncement)
        }
    }

    suspend fun deleteAnnouncement(announcement: Announcement) {
        withContext(Dispatchers.IO) {
            val localAnnouncement = LocalAnnouncement.fromDomain(announcement)
            announcementDao.deleteAnnouncement(localAnnouncement)
        }
    }
}