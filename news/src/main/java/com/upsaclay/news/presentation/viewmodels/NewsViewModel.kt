package com.upsaclay.news.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.repository.AnnouncementRepository
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.RecreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.RefreshAnnouncementUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NewsViewModel(
    private val recreateAnnouncementUseCase: RecreateAnnouncementUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
    private val refreshAnnouncementUseCase: RefreshAnnouncementUseCase,
    announcementRepository: AnnouncementRepository
) : ViewModel() {
    val announcements: Flow<List<Announcement>> = announcementRepository.announcements
        .map { announcements ->
            announcements
                .sortedBy { it.date }
                .map {
                    it.copy(
                        title = it.title?.take(100),
                        content = it.content.take(100)
                    )
                }
        }
    private val _refreshing = MutableSharedFlow<Boolean>()
    val refreshing: SharedFlow<Boolean> = _refreshing

    fun refreshAnnouncements() {
        viewModelScope.launch {
            _refreshing.emit(true)
            refreshAnnouncementUseCase()
            delay(500)
            _refreshing.emit(false)
        }
    }

    fun recreateAnnouncement(announcement: Announcement) {
        recreateAnnouncementUseCase(announcement)
    }

    fun deleteAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            deleteAnnouncementUseCase(announcement)
        }
    }
}