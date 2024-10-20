package com.upsaclay.news.domain.usecase

import com.google.gson.GsonBuilder
import com.upsaclay.common.domain.LocalDateTimeSerializer
import com.upsaclay.news.domain.model.Announcement
import java.time.LocalDateTime

class ConvertAnnouncementToJsonUseCase {
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
        .create()

    fun toJson(announcement: Announcement): String = gson.toJson(announcement)

    fun fromJson(jsonAnnouncement: String): Announcement = gson.fromJson(jsonAnnouncement, Announcement::class.java)
}