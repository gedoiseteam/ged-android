package com.upsaclay.news.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementScreenState
import com.upsaclay.news.domain.entity.AnnouncementState
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.UpdateAnnouncementUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class AnnouncementViewModel(
    announcement: Announcement,
    private val updateAnnouncementUseCase: UpdateAnnouncementUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase
): ViewModel() {
    private val _announcement = MutableStateFlow(announcement)
    val announcement: StateFlow<Announcement> = _announcement
    private val _screenState = MutableStateFlow(AnnouncementScreenState.DEFAULT)
    val screenState: StateFlow<AnnouncementScreenState> = _screenState
    private val _isAnnouncementModified = MutableStateFlow(false)
    val isAnnouncementModified: StateFlow<Boolean> = _isAnnouncementModified
    val currentUser: StateFlow<User?> = getCurrentUserUseCase()
    var title by mutableStateOf(announcement.title ?: "")
        private set
    var content by mutableStateOf(announcement.content)
        private set

    fun updateTitle(title: String) {
        this.title = title
        verifyIsAnnouncementModified()
    }

    fun updateContent(content: String) {
        this.content = content
        verifyIsAnnouncementModified()
    }

    fun deleteAnnouncement() {
        _screenState.value = AnnouncementScreenState.LOADING
        viewModelScope.launch {
            try {
                deleteAnnouncementUseCase(_announcement.value)
            } catch (e: IOException) {
                _screenState.value = AnnouncementScreenState.DELETE_ERROR
            }
        }
    }

    fun updateAnnouncement(announcement: Announcement) {
        _screenState.value = AnnouncementScreenState.LOADING
        viewModelScope.launch {
            try {
                updateAnnouncementUseCase(announcement)
                this@AnnouncementViewModel._announcement.value = announcement
                _screenState.value = AnnouncementScreenState.UPDATED
            } catch (e: Exception) {
                _screenState.value = AnnouncementScreenState.UPDATE_ERROR
            }
        }
    }

    private fun verifyIsAnnouncementModified() {
        val isDifferentTitle = title.trim() != _announcement.value.title
        val isDifferentContent = content.trim() != _announcement.value.content
        _isAnnouncementModified.value = isDifferentTitle || isDifferentContent
    }
}