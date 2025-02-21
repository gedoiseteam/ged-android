package com.upsaclay.news.domain.usecase

import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementState
import com.upsaclay.news.domain.repository.AnnouncementRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository,
    private val scope: CoroutineScope
) {
    operator fun invoke(announcement: Announcement) {
        scope.launch {
            if (announcement.state == AnnouncementState.DEFAULT) {
                announcementRepository.deleteAnnouncement(announcement)
            } else {
                announcementRepository.deleteLocalAnnouncement(announcement)
            }
        }
    }
}