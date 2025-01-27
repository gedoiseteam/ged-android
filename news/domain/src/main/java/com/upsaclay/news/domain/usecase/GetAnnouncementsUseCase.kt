package com.upsaclay.news.domain.usecase

import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.repository.AnnouncementRepository
import kotlinx.coroutines.flow.Flow

class GetAnnouncementsUseCase(
    private val announcementRepository: AnnouncementRepository
) {
    operator fun invoke(): Flow<List<Announcement>> = announcementRepository.announcements
}