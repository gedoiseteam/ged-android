package com.upsaclay.news.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.RecreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.RefreshAnnouncementsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NewsViewModel(
    getAnnouncementsUseCase: GetAnnouncementsUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val refreshAnnouncementsUseCase: RefreshAnnouncementsUseCase,
    private val recreateAnnouncementUseCase: RecreateAnnouncementUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase
) : ViewModel() {
    val announcements: Flow<List<Announcement>> = getAnnouncementsUseCase()
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
    val currentUser: StateFlow<User?> = getCurrentUserUseCase()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun refreshAnnouncements() {
        _isRefreshing.value = true
        viewModelScope.launch {
            refreshAnnouncementsUseCase()
            _isRefreshing.value = false
        }
    }

    fun recreateAnnouncement(announcement: Announcement) {
        recreateAnnouncementUseCase(announcement)
    }

    fun deleteAnnouncement(announcement: Announcement) {
        deleteAnnouncementUseCase(announcement)
    }
}