package com.upsaclay.news.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementScreenState
import com.upsaclay.news.domain.usecase.CreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementFlowUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.RecreateAnnouncementUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import java.io.IOException

class ReadAnnouncementViewModel(
    announcementId: String,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    getAnnouncementUseCase: GetAnnouncementUseCase,
    getAnnouncementFlowUseCase: GetAnnouncementFlowUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
    private val recreateAnnouncementUseCase: RecreateAnnouncementUseCase
) : ViewModel() {
    private val _announcement = MutableStateFlow(getAnnouncementUseCase(announcementId))
    private val _screenState = MutableStateFlow(AnnouncementScreenState.DEFAULT)
    val announcement: StateFlow<Announcement?> = _announcement
    val screenState: StateFlow<AnnouncementScreenState> = _screenState
    val currentUser: StateFlow<User?> = getCurrentUserUseCase()

    init {
        viewModelScope.launch {
            getAnnouncementFlowUseCase(announcementId).collect {
                _announcement.value = it
            }
        }
    }

    fun deleteAnnouncement() {
        if (_announcement.value == null) {
            return
        }

        viewModelScope.launch {
            try {
                deleteAnnouncementUseCase(_announcement.value!!)
                _screenState.value = AnnouncementScreenState.DELETED
            } catch (e: Exception) {
                _screenState.value = AnnouncementScreenState.DELETE_ERROR
            }
        }
    }

    fun recreateAnnouncement(announcement: Announcement) {
        recreateAnnouncementUseCase(announcement)
    }
}