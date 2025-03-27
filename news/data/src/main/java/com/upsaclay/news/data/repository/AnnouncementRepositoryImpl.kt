package com.upsaclay.news.data.repository

import com.upsaclay.news.data.local.AnnouncementLocalDataSource
import com.upsaclay.news.data.remote.AnnouncementRemoteDataSource
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.repository.AnnouncementRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class AnnouncementRepositoryImpl(
    private val announcementRemoteDataSource: AnnouncementRemoteDataSource,
    private val announcementLocalDataSource: AnnouncementLocalDataSource,
    scope: CoroutineScope
) : AnnouncementRepository {
    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    override val announcements: Flow<List<Announcement>> = _announcements

    init {
        scope.launch {
            announcementLocalDataSource.getAnnouncements().collect {
                _announcements.value = it
            }
        }
    }

    override fun getAnnouncement(announcementId: String): Announcement? =
        _announcements.value.firstOrNull { it.id == announcementId }

    override suspend fun refreshAnnouncements() {
        val remoteAnnouncements = runCatching { announcementRemoteDataSource.getAnnouncement() }.getOrElse { return }

        val announcementToDelete = _announcements.value.filterNot { remoteAnnouncements.contains(it) }
        announcementToDelete.forEach { announcementLocalDataSource.deleteAnnouncement(it) }

        val announcementsToUpsert = remoteAnnouncements.filterNot { _announcements.value.contains(it) }
        announcementsToUpsert.forEach { announcementLocalDataSource.upsertAnnouncement(it) }
    }

    override suspend fun createAnnouncement(announcement: Announcement) {
        announcementLocalDataSource.insertAnnouncement(announcement)
        announcementRemoteDataSource.createAnnouncement(announcement)
    }

    override suspend fun createRemoteAnnouncement(announcement: Announcement) {
        announcementRemoteDataSource.createAnnouncement(announcement)
    }

    override suspend fun updateAnnouncement(announcement: Announcement) {
        announcementRemoteDataSource.updateAnnouncement(announcement)
        announcementLocalDataSource.updateAnnouncement(announcement)
    }

    override suspend fun updateAnnouncementState(announcement: Announcement) {
        announcementLocalDataSource.updateAnnouncement(announcement)
    }

    override suspend fun deleteAnnouncement(announcement: Announcement) {
        announcementRemoteDataSource.deleteAnnouncement(announcement.id)
        announcementLocalDataSource.deleteAnnouncement(announcement)
    }

    override suspend fun deleteLocalAnnouncement(announcement: Announcement) {
        announcementLocalDataSource.deleteAnnouncement(announcement)
    }
}