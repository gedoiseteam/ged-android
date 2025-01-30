package com.upsaclay.news.domain.usecase

import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementState
import com.upsaclay.news.domain.repository.AnnouncementRepository

class CreateAnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository
) {
    suspend operator fun invoke(announcement: Announcement) {
        try {
            announcementRepository.createAnnouncement(announcement)
            announcementRepository.updateAnnouncement(announcement.copy(state = AnnouncementState.DEFAULT))
        } catch (e: Exception) {
            announcementRepository.updateAnnouncement(announcement.copy(state = AnnouncementState.ERROR))
        }
    }
}