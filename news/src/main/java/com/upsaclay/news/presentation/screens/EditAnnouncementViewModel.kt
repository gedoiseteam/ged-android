package com.upsaclay.news.presentation.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementScreenState
import com.upsaclay.news.domain.usecase.GetAnnouncementUseCase
import com.upsaclay.news.domain.usecase.UpdateAnnouncementUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditAnnouncementViewModel(
    announcementId: String,
    getAnnouncementUseCase: GetAnnouncementUseCase,
    private val updateAnnouncementUseCase: UpdateAnnouncementUseCase
): ViewModel() {
    private val _announcement = MutableStateFlow(getAnnouncementUseCase(announcementId))
    private val _screenState = MutableStateFlow(AnnouncementScreenState.DEFAULT)
    private val _isAnnouncementModified = MutableStateFlow(false)

    val announcement: StateFlow<Announcement?> = _announcement
    val screenState: StateFlow<AnnouncementScreenState> = _screenState
    val isAnnouncementModified: StateFlow<Boolean> = _isAnnouncementModified

    var title by mutableStateOf(announcement.value?.title ?: "")
        private set
    var content by mutableStateOf(announcement.value?.content ?: "")
        private set

    fun updateTitle(title: String) {
        this.title = title
        verifyIsAnnouncementModified()
    }

    fun updateContent(content: String) {
        this.content = content
        verifyIsAnnouncementModified()
    }

    fun updateAnnouncement(announcement: Announcement) {
        _screenState.value = AnnouncementScreenState.LOADING
        viewModelScope.launch {
            try {
                updateAnnouncementUseCase(announcement)
                this@EditAnnouncementViewModel._announcement.value = announcement
                _screenState.value = AnnouncementScreenState.UPDATED
            } catch (e: Exception) {
                _screenState.value = AnnouncementScreenState.UPDATE_ERROR
            }
        }
    }

    private fun verifyIsAnnouncementModified() {
        val isDifferentTitle = title.trim() != _announcement.value?.title
        val isDifferentContent = content.trim() != _announcement.value?.content
        _isAnnouncementModified.value = isDifferentTitle || isDifferentContent
    }
}