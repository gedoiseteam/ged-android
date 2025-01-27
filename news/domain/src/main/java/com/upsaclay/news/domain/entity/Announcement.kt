package com.upsaclay.news.domain.entity

import com.upsaclay.common.domain.entity.User
import java.time.LocalDateTime

data class Announcement(
    val id: String,
    val title: String? = null,
    val content: String,
    val date: LocalDateTime,
    val author: User,
    val state: AnnouncementState
)