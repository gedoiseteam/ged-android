package com.upsaclay.news.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.GetCurrentUserFlowUseCase
import com.upsaclay.news.domain.model.Announcement
import com.upsaclay.news.domain.model.AnnouncementState
import com.upsaclay.news.domain.usecase.ConvertAnnouncementToJsonUseCase
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAllAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementUseCase
import com.upsaclay.news.domain.usecase.RefreshAnnouncementsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NewsViewModel(
    getAllAnnouncementUseCase: GetAllAnnouncementsUseCase,
    getCurrentUserFlowUseCase: GetCurrentUserFlowUseCase,
    private val refreshAnnouncementsUseCase: RefreshAnnouncementsUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
    private val convertAnnouncementToJsonUseCase: ConvertAnnouncementToJsonUseCase,
    private val getAnnouncementUseCase: GetAnnouncementUseCase
) : ViewModel() {
    private val _announcementState = MutableStateFlow(AnnouncementState.DEFAULT)
    val announcementState: Flow<AnnouncementState> = _announcementState
    val announcements: Flow<List<Announcement>> = getAllAnnouncementUseCase()
    val user: Flow<User> = getCurrentUserFlowUseCase()
    var displayedAnnouncement: Announcement? = null
        private set
    var isRefreshing by mutableStateOf(false)
        private set

    fun refreshAnnouncements() {
        isRefreshing = true
        viewModelScope.launch {
            refreshAnnouncementsUseCase()
            isRefreshing = false
        }
    }

    fun refreshDisplayAnnouncement(id: String) {
        viewModelScope.launch {
            displayedAnnouncement = getAnnouncementUseCase(id)
        }
    }

    fun setDisplayedAnnouncement(announcement: Announcement?) {
        displayedAnnouncement = announcement
    }

    fun convertAnnouncementToJson(announcement: Announcement): String = convertAnnouncementToJsonUseCase.toJson(announcement)

    fun updateAnnouncementState(state: AnnouncementState) {
        _announcementState.value = state
    }

    fun resetAnnouncementState() {
        _announcementState.value = AnnouncementState.DEFAULT
    }

    fun deleteAnnouncement(announcement: Announcement) {
        _announcementState.value = AnnouncementState.LOADING
        viewModelScope.launch {
            delay(300)
            deleteAnnouncementUseCase(announcement)
                .onSuccess { _announcementState.value = AnnouncementState.ANNOUNCEMENT_DELETED }
                .onFailure {
                    _announcementState.value = AnnouncementState.ANNOUNCEMENT_DELETE_ERROR
                }
        }
    }
}