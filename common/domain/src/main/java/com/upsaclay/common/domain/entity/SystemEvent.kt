package com.upsaclay.common.domain.entity

sealed class SystemEvent {
    data class ClearNotifications(val notificationGroupId: String): SystemEvent()
}