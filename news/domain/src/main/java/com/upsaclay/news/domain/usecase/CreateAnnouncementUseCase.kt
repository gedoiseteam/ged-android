package com.upsaclay.news.domain.usecase

import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementState
import com.upsaclay.news.domain.repository.AnnouncementRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CreateAnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository,
    private val scope: CoroutineScope
) {
    operator fun invoke(announcement: Announcement) {
        scope.launch {
            try {
                announcementRepository.createAnnouncement(announcement)
                announcementRepository.updateAnnouncementState(announcement.copy(state = AnnouncementState.DEFAULT))
            } catch (e: Exception) {
                 announcementRepository.updateAnnouncementState(announcement.copy(state = AnnouncementState.ERROR))
            }
        }
    }
}