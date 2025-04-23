package com.upsaclay.common.domain.entity

sealed class SystemEvents {
    data class ClearNotifications(val notificationGroupId: String): SystemEvents()
}