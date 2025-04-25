package com.upsaclay.common.domain.entity

sealed class SharedEvent {
    data class ClearNotifications(val notificationGroupId: String): SharedEvent()
}