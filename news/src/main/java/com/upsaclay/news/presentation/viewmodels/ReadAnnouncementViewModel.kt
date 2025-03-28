package com.upsaclay.news.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementEvent
import com.upsaclay.news.domain.repository.AnnouncementRepository
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementFlowUseCase
import com.upsaclay.news.domain.usecase.RecreateAnnouncementUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException

class ReadAnnouncementViewModel(
    announcementId: String,
    getAnnouncementFlowUseCase: GetAnnouncementFlowUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
    userRepository: UserRepository,
    announcementRepository: AnnouncementRepository
) : ViewModel() {
    private val _announcement = MutableStateFlow(announcementRepository.getAnnouncement(announcementId))
    val announcement: StateFlow<Announcement?> = _announcement
    private val _event = MutableSharedFlow<AnnouncementEvent>()
    val event: SharedFlow<AnnouncementEvent> = _event
    val currentUser: StateFlow<User?> = userRepository.currentUser

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
                _event.emit(AnnouncementEvent.Loading)
                deleteAnnouncementUseCase(_announcement.value!!)
                _event.emit(AnnouncementEvent.Deleted)
            }
            catch (e: ConnectException) {
                _event.emit(AnnouncementEvent.Error(ErrorType.NetworkError))
            } catch (e: Exception) {
                _event.emit(AnnouncementEvent.Error(ErrorType.UnknownError))
            }
        }
    }
}