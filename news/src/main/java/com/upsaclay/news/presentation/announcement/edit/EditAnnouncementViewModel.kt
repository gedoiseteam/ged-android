package com.upsaclay.news.presentation.announcement.edit

import android.accounts.NetworkErrorException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.InternalServerException
import com.upsaclay.common.domain.entity.SingleUiEvent
import com.upsaclay.news.domain.repository.AnnouncementRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException

class EditAnnouncementViewModel(
    announcementId: String,
    private val announcementRepository: AnnouncementRepository
): ViewModel() {
    private val announcement = announcementRepository.getAnnouncement(announcementId)
    private val _uiState = MutableStateFlow(
        EditAnnouncementUiState(
            title = announcement?.title ?: "",
            content = announcement?.content ?: ""
        )
    )
    internal val uiState: StateFlow<EditAnnouncementUiState> = _uiState
    private val _event = MutableSharedFlow<SingleUiEvent>()
    val event: SharedFlow<SingleUiEvent> = _event

    fun onTitleChange(title: String) {
        _uiState.update {
            it.copy(
                title = title,
                enableUpdate = validateUpdate(title, _uiState.value.content)
            )
        }
    }

    fun onContentChange(content: String) {
        _uiState.update {
            it.copy(
                content = content,
                enableUpdate = validateUpdate(_uiState.value.title, content)
            )
        }
    }

    fun updateAnnouncement() {
        val updatedAnnouncement = announcement?.copy(
            title = _uiState.value.title.trim(),
            content = _uiState.value.content.trim()
        ) ?: return

        _uiState.update {
            it.copy(loading = true)
        }

        viewModelScope.launch {
            try {
                announcementRepository.updateAnnouncement(updatedAnnouncement)
                _event.emit(SingleUiEvent.Success())
            } catch (e: Exception) {
                _event.emit(SingleUiEvent.Error(mapErrorMessage(e)))
            } finally {
                _uiState.update {
                    it.copy(loading = false)
                }
            }
        }
    }

    private fun validateUpdate(title: String, content: String): Boolean {
        return validateTitle(title) || validateContent(content)
    }

    private fun validateTitle(title: String): Boolean {
        return title != (announcement?.title ?: "") &&
                _uiState.value.content.isNotBlank()
    }

    private fun validateContent(content: String): Boolean {
        return content != announcement?.content && content.isNotBlank()
    }

    private fun mapErrorMessage(error: Exception): Int {
        return when (error) {
            is ConnectException -> com.upsaclay.common.R.string.server_connection_error
            is SocketTimeoutException -> com.upsaclay.common.R.string.timeout_error
            is InternalServerException -> com.upsaclay.common.R.string.internal_server_error
            is NetworkErrorException -> com.upsaclay.common.R.string.unknown_network_error
            else -> com.upsaclay.common.R.string.unknown_error
        }
    }

    internal data class EditAnnouncementUiState(
        val title: String = "",
        val content: String = "",
        val loading: Boolean = false,
        val enableUpdate: Boolean = false
    )
}