package com.upsaclay.news.domain.usecase

import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.repository.AnnouncementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class GetAnnouncementFlowUseCase(
    private val announcementRepository: AnnouncementRepository
) {
    operator fun invoke(announcementId: String): Flow<Announcement> =
        announcementRepository.announcements.mapNotNull { announcements ->
            announcements.firstOrNull { it.id == announcementId }
        }
}