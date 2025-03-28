package com.upsaclay.news.domain.entity

import com.upsaclay.common.domain.entity.ErrorType

sealed class AnnouncementEvent {
    data object Loading: AnnouncementEvent()
    data object Deleted: AnnouncementEvent()
    data object Updated: AnnouncementEvent()
    data class Error(val type: ErrorType): AnnouncementEvent()
}