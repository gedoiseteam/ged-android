package com.upsaclay.news.data.repository

import com.upsaclay.news.data.local.AnnouncementLocalDataSource
import com.upsaclay.news.data.remote.AnnouncementRemoteDataSource
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementState
import com.upsaclay.news.domain.repository.AnnouncementRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

internal class AnnouncementRepositoryImpl(
    private val announcementRemoteDataSource: AnnouncementRemoteDataSource,
    private val announcementLocalDataSource: AnnouncementLocalDataSource
) : AnnouncementRepository {
    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    override val announcements: Flow<List<Announcement>> = _announcements

    init {
        CoroutineScope(Dispatchers.IO).launch {
            announcementLocalDataSource.getAnnouncements().collect {
                _announcements.value = it
            }
        }
    }

    override suspend fun refreshAnnouncements() {
        val announcements = announcementRemoteDataSource.getAnnouncement()
        if (announcements.isNotEmpty()) {
            val announcementsToUpdate = announcements.filterNot { _announcements.value.contains(it) }
            announcementsToUpdate.forEach { announcementLocalDataSource.upsertAnnouncement(it) }
        }
    }

    override suspend fun createAnnouncement(announcement: Announcement) {
        try {
            announcementLocalDataSource.insertAnnouncement(announcement)
            announcementRemoteDataSource.createAnnouncement(announcement)
        } catch (e: Exception) {
            announcementLocalDataSource.updateAnnouncement(announcement.copy(state = AnnouncementState.ERROR))
        }
    }

    override suspend fun updateAnnouncement(announcement: Announcement) {
        announcementRemoteDataSource.updateAnnouncement(announcement)
        announcementLocalDataSource.updateAnnouncement(announcement)
    }

    override suspend fun deleteAnnouncement(announcement: Announcement) {
        announcementRemoteDataSource.deleteAnnouncement(announcement.id)
        announcementLocalDataSource.deleteAnnouncement(announcement)
    }
}