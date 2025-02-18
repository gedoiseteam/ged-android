package com.upsaclay.news.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementState
import com.upsaclay.news.domain.usecase.CreateAnnouncementUseCase
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CreateAnnouncementViewModel(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val createAnnouncementUseCase: CreateAnnouncementUseCase
) : ViewModel() {
    private var currentUser: User? = null
    var title: String by mutableStateOf("")
        private set
    var content: String by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { currentUser = it }
        }
    }

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateContent(content: String) {
        this.content = content
    }

    fun createAnnouncement() {
        if (currentUser == null) {
            return
        }

        val announcement = Announcement(
            id = GenerateIdUseCase(),
            title = if (title.isBlank()) null else title.trim(),
            content = content.trim(),
            date = LocalDateTime.now(),
            author = currentUser!!,
            state = AnnouncementState.SENDING
        )

        createAnnouncementUseCase(announcement)
    }
}