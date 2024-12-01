package com.upsaclay.news.domain.usecase

import com.upsaclay.news.domain.model.Announcement
import com.upsaclay.news.domain.repository.AnnouncementRepository
import kotlinx.coroutines.flow.Flow

class GetAllAnnouncementsUseCase(
    private val announcementRepository: AnnouncementRepository
) {
    operator fun invoke(): Flow<List<Announcement>> = announcementRepository.announcements
}