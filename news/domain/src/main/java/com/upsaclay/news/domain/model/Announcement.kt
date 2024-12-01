package com.upsaclay.news.domain.model

import com.upsaclay.common.domain.model.User
import java.time.LocalDateTime

data class Announcement(
    val id: String,
    val title: String? = null,
    val content: String,
    val date: LocalDateTime,
    val author: User
) {
    fun isUpdated(announcement: Announcement): Boolean =
        announcement.id == this.id && announcement.date != this.date
}