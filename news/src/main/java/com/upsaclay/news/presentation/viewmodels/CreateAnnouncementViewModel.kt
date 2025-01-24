package com.upsaclay.news.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.GenerateIDUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementScreenState
import com.upsaclay.news.domain.entity.AnnouncementState
import com.upsaclay.news.domain.usecase.CreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.UpdateAnnouncementUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CreateAnnouncementViewModel(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val createAnnouncementUseCase: CreateAnnouncementUseCase,
    private val updateAnnouncementUseCase: UpdateAnnouncementUseCase
): ViewModel() {
    private var user: User? = null
    private val _screenState = MutableStateFlow(AnnouncementScreenState.DEFAULT)
    val screenState: StateFlow<AnnouncementScreenState> = _screenState
    var title: String by mutableStateOf("")
        private set
    var content: String by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user = it }
        }
    }

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateContent(content: String) {
        this.content = content
    }

    fun createAnnouncement() {
        if(user == null) {
            return
        }

        val announcement = Announcement(
            id = GenerateIDUseCase(),
            title = if (title.isBlank()) null else title.trim(),
            content = content.trim(),
            date = LocalDateTime.now(),
            author = user!!,
            state = AnnouncementState.LOADING
        )

        _screenState.value = AnnouncementScreenState.LOADING
        viewModelScope.launch {
            try {
                createAnnouncementUseCase(announcement)
                updateAnnouncementUseCase(announcement.copy(state = AnnouncementState.DEFAULT))
                _screenState.value = AnnouncementScreenState.CREATED
            } catch (e: Exception) {
                _screenState.value = AnnouncementScreenState.CREATION_ERROR
            }
        }
    }
}