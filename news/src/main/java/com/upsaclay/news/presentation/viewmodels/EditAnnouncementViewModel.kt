package com.upsaclay.news.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementEvent
import com.upsaclay.news.domain.repository.AnnouncementRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.net.ConnectException

class EditAnnouncementViewModel(
    announcementId: String,
    private val announcementRepository: AnnouncementRepository
): ViewModel() {
    private val _announcement = MutableStateFlow(announcementRepository.getAnnouncement(announcementId))
    val announcement: StateFlow<Announcement?> = _announcement
    private val _event = MutableSharedFlow<AnnouncementEvent>()
    val event: SharedFlow<AnnouncementEvent> = _event
    private val _isAnnouncementModified = MutableStateFlow(false)
    val isAnnouncementModified: StateFlow<Boolean> = _isAnnouncementModified

    private val _title = MutableStateFlow(announcement.value?.title)
    val title: StateFlow<String?> = _title
    private val _content = MutableStateFlow(announcement.value?.content ?: "")
    val content: StateFlow<String> = _content

    init {
        checkModifiedAnnouncement()
    }

    fun updateTitle(title: String) {
        this._title.value = title
    }

    fun updateContent(content: String) {
        this._content.value = content
    }

    fun updateAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            _event.emit(AnnouncementEvent.Loading)
            try {
                announcementRepository.updateAnnouncement(announcement)
                this@EditAnnouncementViewModel._announcement.value = announcement
                _event.emit(AnnouncementEvent.Updated)
            } catch (e: ConnectException) {
                _event.emit(AnnouncementEvent.Error(ErrorType.NetworkError))
            } catch (e: Exception) {
                _event.emit(AnnouncementEvent.Error(ErrorType.UnknownError))
            }
        }
    }

    private fun checkModifiedAnnouncement() {
        combine(_title, _content) { title, content ->
            val isDifferentTitle = title?.trim() != _announcement.value?.title
            val isDifferentContent = content.trim() != _announcement.value?.content
            _isAnnouncementModified.value = isDifferentTitle || isDifferentContent
        }.launchIn(viewModelScope)
    }
}