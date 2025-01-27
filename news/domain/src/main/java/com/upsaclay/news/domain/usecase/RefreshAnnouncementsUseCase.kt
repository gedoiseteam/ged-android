package com.upsaclay.news.domain.usecase

import com.upsaclay.news.domain.repository.AnnouncementRepository

class RefreshAnnouncementsUseCase(
    private val announcementRepository: AnnouncementRepository
) {
    suspend operator fun invoke() {
        announcementRepository.refreshAnnouncements()
    }
}