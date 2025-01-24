package com.upsaclay.news.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementScreenState
import com.upsaclay.news.domain.usecase.ConvertAnnouncementToJsonUseCase
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.RefreshAnnouncementsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel(
    getAllAnnouncementUseCase: GetAnnouncementsUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val refreshAnnouncementsUseCase: RefreshAnnouncementsUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
    private val convertAnnouncementToJsonUseCase: ConvertAnnouncementToJsonUseCase
) : ViewModel() {
    private val _announcementScreenState = MutableStateFlow(AnnouncementScreenState.DEFAULT)
    val announcementScreenState: Flow<AnnouncementScreenState> = _announcementScreenState
    val announcements: Flow<List<Announcement>> = getAllAnnouncementUseCase()
    val user: Flow<User?> = getCurrentUserUseCase()
    var isRefreshing by mutableStateOf(false)
        private set

    fun refreshAnnouncements() {
        isRefreshing = true
        viewModelScope.launch {
            refreshAnnouncementsUseCase()
            isRefreshing = false
        }
    }

    fun convertAnnouncementToJson(announcement: Announcement): String =
        convertAnnouncementToJsonUseCase.toJson(announcement)

    fun updateAnnouncementState(state: AnnouncementScreenState) {
        _announcementScreenState.value = state
    }

    fun resetAnnouncementState() {
        _announcementScreenState.value = AnnouncementScreenState.DEFAULT
    }
}