package com.upsaclay.news.domain.usecase

import com.upsaclay.news.domain.model.Announcement
import com.upsaclay.news.domain.repository.AnnouncementRepository

class DeleteAnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository
) {
    suspend operator fun invoke(announcement: Announcement): Result<Unit> =
        announcementRepository.deleteAnnouncement(announcement)
}