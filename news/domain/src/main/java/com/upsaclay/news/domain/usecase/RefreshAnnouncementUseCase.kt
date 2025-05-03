package com.upsaclay.news.domain.usecase

import com.upsaclay.news.domain.repository.AnnouncementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class RefreshAnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository
) {
    private var lastRequestTime: Long = 0
    private val debounceInterval = 3000L
    private val _refreshing = MutableStateFlow(false)
    val refreshing: Flow<Boolean> = _refreshing

    suspend operator fun invoke() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastRequestTime > debounceInterval) {
            _refreshing.emit(true)
            announcementRepository.refreshAnnouncements()
            lastRequestTime = currentTime
        }

        _refreshing.emit(false)
    }
}