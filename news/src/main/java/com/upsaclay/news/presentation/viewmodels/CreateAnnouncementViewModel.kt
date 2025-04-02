package com.upsaclay.news.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.upsaclay.common.domain.entity.User
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementState
import com.upsaclay.news.domain.usecase.CreateAnnouncementUseCase
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

class CreateAnnouncementViewModel(
    userRepository: UserRepository,
    private val createAnnouncementUseCase: CreateAnnouncementUseCase
) : ViewModel() {
    private var currentUser: StateFlow<User?> = userRepository.currentUser
    var title: String by mutableStateOf("")
        private set
    var content: String by mutableStateOf("")
        private set

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateContent(content: String) {
        this.content = content
    }

    fun createAnnouncement() {
        if (currentUser.value == null) {
            return
        }

        val announcement = Announcement(
            id = GenerateIdUseCase.asString(),
            title = if (title.isBlank()) null else title.trim(),
            content = content.trim(),
            date = LocalDateTime.now(),
            author = currentUser.value!!,
            state = AnnouncementState.SENDING
        )

        createAnnouncementUseCase(announcement)
    }
}