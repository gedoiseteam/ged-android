package com.upsaclay.news.domain.usecase

import com.upsaclay.news.domain.repository.AnnouncementRepository

class RefreshAnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository
) {
    private var lastRequestTime: Long = 0
    private val debounceInterval = 3000L

    suspend operator fun invoke() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastRequestTime > debounceInterval) {
            announcementRepository.refreshAnnouncements()
            lastRequestTime = currentTime
        }
    }
}