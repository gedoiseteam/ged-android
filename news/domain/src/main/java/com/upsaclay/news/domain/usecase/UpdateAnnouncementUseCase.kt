package com.upsaclay.news.domain.usecase

import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.repository.AnnouncementRepository

class UpdateAnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository
) {
    suspend operator fun invoke(announcement: Announcement) {
        announcementRepository.updateAnnouncement(announcement)
    }
}