package com.upsaclay.news.domain.usecase

import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementState
import com.upsaclay.news.domain.repository.AnnouncementRepository

class DeleteAnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository
) {
    suspend operator fun invoke(announcement: Announcement) {
        if (announcement.state == AnnouncementState.PUBLISHED) {
            announcementRepository.deleteAnnouncement(announcement)
        } else {
            announcementRepository.deleteLocalAnnouncement(announcement)
        }
    }
}