package com.upsaclay.news.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.usecase.CreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.RecreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.RefreshAnnouncementsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NewsViewModel(
    getAnnouncementsUseCase: GetAnnouncementsUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val refreshAnnouncementsUseCase: RefreshAnnouncementsUseCase
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
    var isRefreshing by mutableStateOf(false)
        private set

    fun refreshAnnouncements() {
        isRefreshing = true
        viewModelScope.launch {
            refreshAnnouncementsUseCase()
            isRefreshing = false
        }
    }
}